package model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Dao {
	private static Dao instance = new Dao();

	public static Dao getInstance() {
		return instance;
	}
	private static Connection conn = null;

	// DB Connection OPEN
	private Connection getConnection() {
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@203.233.196.163:1521:xe";
		String id = "ora2nd";
		String password = "test";

		try {
			synchronized (DriverManager.class) {
				Class.forName(driver);
				conn = DriverManager.getConnection(url, id, password);
			}
		} catch (ClassNotFoundException e) {
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	// DB Connection CLOSE
	public static void freeConnection() {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	static ResultSet rs = null;
	static PreparedStatement pstmt = null;

	public List<AuctionBean> getAuction() {
		List<AuctionBean> auctionList = null;
		conn = getConnection();

		if (conn != null) {
			try {
				String sql = "select ";
				sql += "AUCTION_NUM, AUCTION_ITEM_ID, ";
				sql += "to_char(AUCTION_END_DAY, 'yyyy-mm-dd hh24:mi:ss') END_DAY, ";
				sql += "AUCTION_NOW_PRICE, AUCTION_MEMBER_ID ";
				sql += "from AUCTION ";
				sql += "where AUCTION_STATUS='진행'";
				pstmt = conn.prepareStatement(sql);

				rs = pstmt.executeQuery();
				if (rs.next()) {
					auctionList = new ArrayList<AuctionBean>();
					do {
						AuctionBean ab = new AuctionBean();
						ab.setAuction_num(rs.getInt("AUCTION_NUM"));
						ab.setAuction_item_id(rs.getInt("AUCTION_ITEM_ID"));
						ab.setAuction_end_day(rs.getString("END_DAY"));
						ab.setAuction_now_price(rs.getInt("AUCTION_NOW_PRICE"));
						ab.setAuction_member_id(rs
								.getString("AUCTION_MEMBER_ID"));

						auctionList.add(ab);
					} while (rs.next());
				}
			} catch (Exception e) {
				System.out.println("SQL오류" + e.toString());
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (pstmt != null) {
						pstmt.close();
					}
					freeConnection();
				} catch (SQLException e) {
				}
			}
		}
		return auctionList;
	}

	
	public int setRequest(String sqlStatement) {
		conn = getConnection();
		int cnt = -1;

		if (conn != null) {
			try {
				conn.setAutoCommit(false);
				pstmt = conn.prepareStatement(sqlStatement);

				cnt = pstmt.executeUpdate();

				conn.commit();
			} catch (SQLException e) {
				System.out.println("SQL오류" + e.toString());

			} finally {
				try {
					if (pstmt != null)
						pstmt.close();
					freeConnection();
				} catch (SQLException e) {
				}
			}
		}
		return cnt;
	}
}
