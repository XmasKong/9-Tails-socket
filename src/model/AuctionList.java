package model;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class AuctionList {
	//AuctionList is now in progress
	private static Map<Integer, AuctionBean> auctionList;
	
	public AuctionList() {
		//Get AuctionList which is now in progress
		if(getAuctionList() == null) {
			refreshAuction();
		}
		
		Iterator<Integer> iterator = getAuctionList().keySet().iterator();
		
		while(iterator.hasNext()) {
			int key = iterator.next();
			AuctionBean ab = getAuctionList().get(key); 
			System.out.println(ab.getAuction_num());
			String duration = checkDate(ab.getAuction_end_day());
			if(duration.equals("EndAuction")) {
				//End Auction
				Dao dao = Dao.getInstance();
				String sql = "";
				//Update Auction Database
				if(getAuctionList().get(key).getAuction_member_id()==null){
					//not enough bid
					sql = "update AUCTION set AUCTION_STATUS='미달' where auction_num = " + ab.getAuction_num();
					dao.setRequest(sql);
					//add item count(+1)
					sql = "update item set item_count = item_count+1 where item_num = " + ab.getAuction_item_id();
					dao.setRequest(sql);
				} else {
					//successful bid
					sql = "update AUCTION set AUCTION_STATUS='마감' where auction_num = " + ab.getAuction_num();
					dao.setRequest(sql);
					//insert pay
					sql = "insert into PAYMENT (PAYMENT_NUM, PAYMENT_STATUS, PAYMENT_PRICE, MEMBER_NUM, AUCTION_NUM)";
					sql += "values ( ";
					sql += "PAYMENT_SEQ.NEXTVAL, '주문대기', " + ab.getAuction_now_price() + ", ";
					sql += "(select member_num from bid where bid_price = ";
					sql += "(select max(bid_price) from bid where auction_num = " + ab.getAuction_num() + ")), ";
					sql += ab.getAuction_num() + ")";
					dao.setRequest(sql);
				}
				refreshAuction();
			} else {
				ab.setAuction_duration(duration);
				getAuctionList().put(key, ab); 
			}
		}
	}
	
	/**
	 * Update AuctionList which is now in progress
	 */
	public void refreshAuction() {
		setAuctionList(new HashMap<Integer, AuctionBean>());
		Dao dao = Dao.getInstance();
					
		List<AuctionBean> result = dao.getAuction();
		if(result != null) {			
			for (AuctionBean auctionBean : result) {
				getAuctionList().put(auctionBean.getAuction_num(), auctionBean);
			}
		}
	}
	
	/**
	 * Check Auction End Date
	 * @param end_date
	 * @return
	 */
	public String checkDate(String end_date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = null;
		try {
			date1 = sdf.parse(end_date);
		} catch (ParseException e) {
			System.out.println("날짜 형식 오류");
			e.printStackTrace();
		}
		
		Date date2 = new Date();
		
		long ldate1 = date1.getTime();
		long ldate2 = date2.getTime();
		
		long diff;
		diff = ldate1 - ldate2;
		if(diff < 0 ) {
			return "EndAuction";
		} else {
			//MilliSecond는 1초의 1000/1
		    long hour = diff/1000/60/60;
		    long min = diff/1000/60%60;
		    long second = diff/1000%60%60;
		    
		    String duration = "";
		    if(hour < 10)
		    	duration += "0";
		    
		    duration += hour + ":";
		   
		    if(min < 10)
		    	duration += "0";
		    
		    duration += min + ":";
		    
		    if(second <10)
		    	duration += "0";
		    
		    duration += second;
		    System.out.println(duration);
			return duration;
		}
	}

	public static Map<Integer, AuctionBean> getAuctionList() {
		return auctionList;
	}

	public static void setAuctionList(Map<Integer, AuctionBean> auctionList) {
		AuctionList.auctionList = auctionList;
	}
}
