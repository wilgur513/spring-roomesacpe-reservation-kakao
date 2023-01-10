package nextstep.console;

import nextstep.model.Reservation;
import nextstep.model.Theme;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class ReservationDAO {

    public Reservation addReservation(Reservation reservation) {
        String sql = "INSERT INTO reservation (date, time, name, theme_name, theme_desc, theme_price) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection con = createConnection();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"})
        ) {
            ps.setDate(1, Date.valueOf(reservation.getDate()));
            ps.setTime(2, Time.valueOf(reservation.getTime()));
            ps.setString(3, reservation.getName());
            ps.setString(4, reservation.getTheme().getName());
            ps.setString(5, reservation.getTheme().getDesc());
            ps.setInt(6, reservation.getTheme().getPrice());
            ps.executeUpdate();

            return new Reservation(getId(ps), reservation.getDate(), reservation.getTime(), reservation.getName(), reservation.getTheme());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Reservation findReservation(Long id) {
        String sql = "SELECT date, time, name, theme_name, theme_desc, theme_price FROM reservation WHERE id = ?";

        try (Connection con = createConnection();
             PreparedStatement ps = con.prepareStatement(sql)
        ) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                LocalDate date = rs.getDate("date").toLocalDate();
                LocalTime time = rs.getTime("time").toLocalTime();
                String name = rs.getString("name");
                String themeName = rs.getString("theme_name");
                String themeDesc = rs.getString("theme_desc");
                Integer themePrice = rs.getInt("theme_price");
                return new Reservation(id, date, time, name, new Theme(themeName, themeDesc, themePrice));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public void deleteReservation(Long id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (Connection con = createConnection();
             PreparedStatement ps = con.prepareStatement(sql);
        ) {
            ps.setLong(1, id);
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static Long getId(PreparedStatement ps) throws SQLException {
        try(ResultSet rs = ps.getGeneratedKeys()) {
            rs.next();
            return rs.getLong("id");
        }
    }

    private static Connection createConnection() {
        try {
            Connection con = DriverManager.getConnection("jdbc:h2:~/test;AUTO_SERVER=true", "sa", "");
            System.out.println("정상적으로 연결되었습니다.");
            return con;
        } catch (SQLException e) {
            System.err.println("연결 오류:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
