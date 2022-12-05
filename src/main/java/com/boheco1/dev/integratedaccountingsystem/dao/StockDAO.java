package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import org.openxmlformats.schemas.drawingml.x2006.main.STSystemColorVal;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {

    private static final int CRITICAL = 50;
    /**
     * Create a new Stock record
     * @param stock the Stock record to be created
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void add(Stock stock) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO Stocks " +
                        "(StockName, Description, SerialNumber, " +
                        "Brand, Model, ManufacturingDate, " +
                        "ValidityDate, TypeID, Unit, " +
                        "Quantity, Price, NEACode, " +
                        "IsTrashed, Comments, CreatedAt, " +
                        "UserIDCreated, Critical, id, LocalCode, AcctgCode, Individualized, localDescription) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,GETDATE(),?,?,?,?,?,?,?)");

        if(stock.getId()==null) stock.setId(Utility.generateRandomId());

        ps.setString(1, stock.getStockName());
        ps.setString(2, stock.getDescription());
        ps.setString(3, stock.getSerialNumber());

        ps.setString(4, stock.getBrand());
        ps.setString(5, stock.getModel());
        ps.setDate(6, stock.getManufacturingDate()!=null ? Date.valueOf(stock.getManufacturingDate()) : null);

        ps.setDate(7, stock.getValidityDate()!=null ? Date.valueOf(stock.getValidityDate()) : null);
        ps.setString(8, stock.getTypeID());
        ps.setString(9, stock.getUnit());

        ps.setDouble(10, stock.getQuantity());
        ps.setDouble(11, stock.getPrice());
        ps.setString(12, stock.getNeaCode());

        ps.setBoolean(13, stock.isTrashed());
        ps.setString(14, stock.getComments());
        ps.setString(15, stock.getUserIDCreated());

        ps.setInt(16, stock.getCritical());

        ps.setString(17, stock.getId());

        ps.setString(18, stock.getLocalCode());
        ps.setString(19, stock.getAcctgCode());

        ps.setBoolean(20, stock.isIndividualized());
        ps.setString(21, stock.getLocalDescription());
        ps.executeUpdate();

        ps.close();
    }

    /**
     * Updates the product prices
     * @param stocks the list of stocks to update
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void batchUpdatePrices(List<Stock> stocks) throws Exception{
        String sql = "";
        for (Stock stock : stocks) {
            sql += "UPDATE Stocks SET Price = '" + stock.getPrice() + "' WHERE id= '" + stock.getId() + "'; ";
            sql += "INSERT INTO StockHistory (id, StockID, date, price, updatedBy) " +
                    "VALUES ('" + Utility.generateRandomId() + "', '" + stock.getId() + "','" + LocalDate.now() + "','" + stock.getOldPrice() + "','" + ActiveUser.getUser().getId() + "'); ";
        }
        Connection conn = DB.getConnection();

        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.executeUpdate();
            conn.commit();
            ps.close();
        } catch (SQLException e) {
            conn.rollback();
            conn.close();
        }
    }

    /**
     * Retrieves a single Stock record
     * @param code the local code or nea code identifier of the Stock record to be retrieved
     * @param brand the brand identifier of the Stock record to be retrieved
     * @return Stock object
     * @throws Exception obligatory from DB.getConnection()
     */
    public static Stock get(String code,String desc, String brand) throws Exception {
        PreparedStatement ps = null;
        if (brand == null || brand.isEmpty() || brand.equals("")) {
            ps = DB.getConnection().prepareStatement(
                    "SELECT * FROM Stocks WHERE id=(SELECT id FROM Stocks WHERE (localCode=? OR neacode=?) AND Description=? AND brand IS NULL)");
            ps.setString(1, code);
            ps.setString(2, code);
            ps.setString(3, desc);
        }else{
            ps = DB.getConnection().prepareStatement(
                    "SELECT * FROM Stocks WHERE id=(SELECT id FROM Stocks WHERE (localCode=? OR neacode=?) AND Description=? AND brand=?)");
            ps.setString(1, code);
            ps.setString(2, code);
            ps.setString(3, desc);
            ps.setString(4, brand);
        }
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            Stock stock = new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            );
            stock.setLocalDescription(rs.getString("localDescription"));
            rs.close();

            return stock;
        }
        rs.close();
        return null;
    }

    /**
     * Retrieves a single Stock record
     * @param id the identifier of the Stock record to be retrieved
     * @return Stock object
     * @throws Exception obligatory from DB.getConnection()
     */
    public static Stock get(String id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Stocks WHERE id=?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            Stock stock = new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getDouble("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            );
            stock.setLocalDescription(rs.getString("localDescription"));
            rs.close();

            return stock;
        }
        rs.close();
        return null;
    }

    /**
     * Retrieves a single Stock record
     * @param code the identifier of the Stock record to be retrieved
     * @return Stock object
     * @throws Exception obligatory from DB.getConnection()
     */
    public static Stock getStockViaNEALocalCode(String code) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT id, Description, Unit, Price, localDescription FROM Stocks WHERE (NEACode=? OR LocalCode=?) ");
        ps.setString(1, code);
        ps.setString(2, code);

        PreparedStatement ps2 = DB.getConnection().prepareStatement(
                "SELECT SUM(Quantity) as Qty FROM Stocks WHERE (NEACode=? OR LocalCode=?) ");
        ps2.setString(1, code);
        ps2.setString(2, code);

        ResultSet rs = ps.executeQuery();
        ResultSet rs2 = ps2.executeQuery();
        if(rs.next()) {
            Stock stock = new Stock();
            stock.setId(rs.getString("id"));
            stock.setDescription(rs.getString("Description"));
            stock.setUnit(rs.getString("Unit"));
            stock.setPrice(rs.getDouble("Price"));
            stock.setLocalDescription( rs.getString("localDescription"));
            if(rs2.next()) {
                stock.setQuantity(rs2.getDouble("Qty"));
            }
            rs.close();
            rs2.close();
            return stock;
        }
        rs.close();
        return null;
    }

    public static double getTotalStockViaNEALocalCode(String code) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT SUM(Stocks.Quantity) as Qty FROM Stocks WHERE NEACode=? OR LocalCode=? AND Quantity > 0 ");
        ps.setString(1, code);
        ps.setString(2, code);
        ResultSet rs = ps.executeQuery();
        double qty = 0;

        if(rs.next()) {
            qty = rs.getDouble("Qty");
        }
        rs.close();
        return qty;
    }


    /**
     * Updates an existing Stock record
     * @param stock the record to be updated
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void update(Stock stock) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE Stocks SET " +
                        "StockName=?, Description=?, SerialNumber=?," +
                        "Brand=?, Model=?, ManufacturingDate=?, " +
                        "ValidityDate=?, TypeID=?, Unit=?," +
                        "Quantity=?, Price=?, NEACode=?," +
                        "Comments=?, UpdatedAt=GETDATE(), UserIDCreated=?, Critical=?," +
                        "LocalCode=?, AcctgCode=?, Individualized=?, localDescription=? " +
                        "WHERE id=?");

        Stock oldStock = StockDAO.get(stock.getId());

        ps.setString(1, stock.getStockName());
        ps.setString(2, stock.getDescription());
        ps.setString(3, stock.getSerialNumber());

        ps.setString(4, stock.getBrand());
        ps.setString(5, stock.getModel());

        try {
            ps.setDate(6, Date.valueOf(stock.getManufacturingDate()));
        }catch(Exception e){
            ps.setDate(6, null);
        }

        try {
            ps.setDate(7, Date.valueOf(stock.getValidityDate()));
        }catch(Exception e){
            ps.setDate(7, null);
        }

        ps.setString(8,stock.getTypeID());
        ps.setString(9, stock.getUnit());

        ps.setDouble(10, stock.getQuantity());
        ps.setDouble(11, stock.getPrice());
        ps.setString(12, stock.getNeaCode());

        ps.setString(13, stock.getComments());
        ps.setString(14, ActiveUser.getUser().getId());
        ps.setInt(15, stock.getCritical());
        ps.setString(16, stock.getLocalCode());
        ps.setString(17, stock.getAcctgCode());
        ps.setBoolean(18, stock.isIndividualized());
        ps.setString(19, stock.getLocalDescription());
        ps.setString(20, stock.getId());
        ps.executeUpdate();

        //create a Stock History if price was updated.
        if(oldStock.getPrice()!=stock.getPrice()) {
            StockHistoryDAO.create(
                    new StockHistory(
                            Utility.generateRandomId(),
                            stock.getId(),
                            LocalDate.now().minusDays(1),
                            oldStock.getPrice(),
                            ActiveUser.getUser().getId()
                    )
            );
        }

        ps.close();
    }

    /**
     * Updates an existing Stock record
     * @param stock the record to be updated
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void updateDetails(Stock stock) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE Stocks SET " +
                        "StockName=?, Description=?, SerialNumber=?," +
                        "Brand=?, Model=?, ManufacturingDate=?, " +
                        "ValidityDate=?, TypeID=?, Unit=?," +
                        "Price=?, NEACode=?," +
                        "Comments=?, UpdatedAt=GETDATE(), UserIDCreated=?, " +
                        "LocalCode=?, AcctgCode=? " +
                        "WHERE id=?");
        Stock oldStock = StockDAO.get(stock.getId());
        ps.setString(1, stock.getStockName());
        ps.setString(2, stock.getDescription());
        ps.setString(3, stock.getSerialNumber());

        ps.setString(4, stock.getBrand());
        ps.setString(5, stock.getModel());

        if (stock.getManufacturingDate() != null) {
            ps.setDate(6, Date.valueOf(stock.getManufacturingDate()));
        } else {
            ps.setDate(6, null);
        }

        if (stock.getValidityDate() != null) {
            ps.setDate(7, Date.valueOf(stock.getValidityDate()));
        } else {
            ps.setDate(7, null);
        }

        ps.setString(8, stock.getTypeID());
        ps.setString(9, stock.getUnit());

        ps.setDouble(10, stock.getPrice());
        ps.setString(11, stock.getNeaCode());

        ps.setString(12, stock.getComments());
        ps.setString(13, ActiveUser.getUser().getId());
        ps.setString(14, stock.getLocalCode());
        ps.setString(15, stock.getAcctgCode());
        ps.setString(16, stock.getId());

        ps.executeUpdate();

        //create a Stock History if price was updated.
        if (oldStock.getPrice() != stock.getPrice()) {
            StockHistoryDAO.create(new StockHistory(
                    Utility.generateRandomId(),
                    stock.getId(),
                    LocalDate.now().minusDays(1),
                    oldStock.getPrice(),
                    ActiveUser.getUser().getId()
            ));
        }
        ps.close();
    }

    /**
     * Retrieves a list of SlimStocks as a search result based on a search Key
     * @param key The search key
     * @param trashed Stock status whether trashed or not
     * @return A list of SlimStock that qualifies with the search key
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<SlimStock> search(String key, int trashed) throws Exception  {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "Select TOP 50 id, StockName, Brand, Model, Description, localDescription, Price, Unit, Quantity, NEACode, LocalCode FROM Stocks " +
                        "WHERE (StockName LIKE ? OR Description LIKE ? OR Brand LIKE ? OR Model LIKE ? OR localDescription LIKE ?) " +
                        "AND IsTrashed=? ORDER BY Description");
        ps.setString(1, "%" + key + "%");
        ps.setString(2, "%" + key + "%");
        ps.setString(3, "%" + key + "%");
        ps.setString(4, "%" + key + "%");
        ps.setString(5, "%" + key + "%");
        ps.setInt(6, trashed);

        ResultSet rs = ps.executeQuery();

        ArrayList<SlimStock> stocks = new ArrayList<>();
        while(rs.next()) {
            SlimStock stock = new SlimStock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Model"),
                    rs.getString("Brand"));
            stock.setDescription(rs.getString("Description"));
            stock.setLocalDescription(rs.getString("localDescription"));
            stock.setPrice(rs.getDouble("Price"));
            stock.setUnit(rs.getString("Unit"));
            stock.setQuantity(rs.getInt("Quantity"));
            String neaCode = rs.getString("NeaCode");
            if (neaCode != null && neaCode.length() != 0) {
                stock.setCode(neaCode);
            }else{
                stock.setCode(rs.getString("LocalCode"));
            }
            stocks.add(stock);
        }

        rs.close();
        ps.close();

        return stocks;
    }

    /**
     * Retrieves a list of available SlimStocks as a search result based on a search Key
     * @param key The search key
     * @return A list of SlimStock that qualifies with the search key
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<SlimStock> search_available(String key) throws Exception  {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "Select TOP 50 id, StockName, Brand, Model, Description, Price, Unit, Quantity, NEACode, LocalCode FROM Stocks " +
                        "WHERE (StockName LIKE ? OR Description LIKE ? OR Brand LIKE ? OR Model LIKE ? ) " +
                        "AND IsTrashed=0 AND Quantity > 0 ORDER BY Description");
        ps.setString(1, "%" + key + "%");
        ps.setString(2, "%" + key + "%");
        ps.setString(3, "%" + key + "%");
        ps.setString(4, "%" + key + "%");

        ResultSet rs = ps.executeQuery();

        ArrayList<SlimStock> stocks = new ArrayList<>();
        while(rs.next()) {
            SlimStock stock = new SlimStock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Model"),
                    rs.getString("Brand"));
            stock.setDescription(rs.getString("Description"));
            stock.setPrice(rs.getDouble("Price"));
            stock.setUnit(rs.getString("Unit"));
            stock.setQuantity(rs.getInt("Quantity"));
            String neaCode = rs.getString("NEACode");
            if (neaCode != null && neaCode.length() != 0) {
                stock.setCode(rs.getString("NEACode"));
            }else{
                stock.setCode(rs.getString("LocalCode"));
            }
            stocks.add(stock);
        }

        rs.close();
        ps.close();

        return stocks;
    }

    /**
     * Retrieves a list of available SlimStocks as a search result based on a search Key
     * @param key The search key
     * @return A list of SlimStock that qualifies with the search key
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<SlimStock> search_available_in_rr(String key) throws Exception  {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "Select TOP 50 Stocks.id, StockName, Brand, Model, Description, localDescription, StockEntryLogs.Price, Unit, Stocks.Quantity as Quantity, RRNo, NEACode, LocalCode FROM Stocks INNER JOIN StockEntryLogs ON Stocks.id = StockEntryLogs.StockID " +
                        "WHERE (StockName LIKE ? OR Description LIKE ? OR Brand LIKE ? OR Model LIKE ? OR localDescription LIKE ?) AND RRno IS NOT NULL " +
                        "AND IsTrashed=0 AND Stocks.Quantity > 0 ORDER BY RRNo");
        ps.setString(1, "%" + key + "%");
        ps.setString(2, "%" + key + "%");
        ps.setString(3, "%" + key + "%");
        ps.setString(4, "%" + key + "%");
        ps.setString(5, "%" + key + "%");

        ResultSet rs = ps.executeQuery();

        ArrayList<SlimStock> stocks = new ArrayList<>();
        while(rs.next()) {
            SlimStock stock = new SlimStock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Model"),
                    rs.getString("Brand"));
            stock.setDescription(rs.getString("Description"));
            stock.setLocalDescription(rs.getString("localDescription"));
            stock.setPrice(rs.getDouble("Price"));
            stock.setUnit(rs.getString("Unit"));
            stock.setQuantity(rs.getInt("Quantity"));
            stock.setRRNo(rs.getString("RRNo"));
            String neaCode = rs.getString("NEACode");
            if (neaCode != null && neaCode.length() != 0) {
                stock.setCode(rs.getString("NEACode"));
            }else{
                stock.setCode(rs.getString("LocalCode"));
            }
            stocks.add(stock);
        }

        rs.close();
        ps.close();

        return stocks;
    }

    /**
     * Get a list of Stocks with offset and limit. Used in viewing list stocks.
     * @param limit number of rows to fetch
     * @param offset number of rows to skip
     * @param trashed Stock status whether trashed or not
     * @return ArrayList of SlimStocks
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<SlimStock> getSlimStockList(int limit, int offset, int trashed) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "Select * FROM Stocks " +
                        "WHERE IsTrashed=? "+
                        "ORDER BY Description " +
                        "OFFSET ? ROWS " +
                        "FETCH NEXT ? ROWS ONLY");
        ps.setInt(1, trashed);
        ps.setInt(2, offset);
        ps.setInt(3, limit);

        ResultSet rs = ps.executeQuery();

        ArrayList<SlimStock> stocks = new ArrayList<>();

        while(rs.next()) {
            SlimStock stock = new SlimStock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Model"),
                    rs.getString("Brand"));

            stock.setDescription(rs.getString("Description"));
            stock.setLocalDescription(rs.getString("localDescription"));
            stock.setPrice(rs.getDouble("Price"));
            stock.setUnit(rs.getString("Unit"));
            stock.setQuantity(rs.getInt("Quantity"));
            String neaCode = rs.getString("NeaCode");
            if (neaCode != null && neaCode.length() != 0) {
                stock.setCode(neaCode);
            }else{
                stock.setCode(rs.getString("LocalCode"));
            }
            stocks.add(stock);
        }

        rs.close();
        ps.close();

        return stocks;
    }

    /**
     * Get a list of Stocks with offset and limit
     * @param limit number of rows to fetch
     * @param offset number of rows to skip
     * @return ArrayList of Stocks
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Stock> getList(int limit, int offset) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "Select * FROM Stocks " +
                        "WHERE IsTrashed=0 " +
                        "ORDER BY UpdatedAt, StockName " +
                        "OFFSET ? ROWS " +
                        "FETCH NEXT ? ROWS ONLY");
        ps.setInt(1, offset);
        ps.setInt(2, limit);

        ResultSet rs = ps.executeQuery();

        ArrayList<Stock> stocks = new ArrayList<>();

        while(rs.next()) {
            stocks.add(new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            ));
        }

        rs.close();
        ps.close();

        return stocks;
    }

    /**
     * Change the quantity of a Stock as a result of entry of release.
     * @param stock The stock record to be updated
     * @param quantity the new quantity to given to the stock
     * @throws Exception obligatory from DB.getConnection()
     */
    private static void changeQuantity(Stock stock, double quantity) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE Stocks SET quantity=?, UpdatedAt=GETDATE(), UserIDUpdated=? " +
                        "WHERE id = ?");
        ps.setDouble(1, quantity);
        ps.setString(2, ActiveUser.getUser().getId());
        ps.setString(3, stock.getId());

        ps.executeUpdate();

        ps.close();
    }

    /**
     * Add quantity to a stock as a result of stock entry
     * @param stock the stock record to be updated
     * @param quantity the quantity to be added to the stock
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void addStockQuantity(Stock stock, double quantity) throws Exception {
        double newQty = stock.getQuantity() + quantity;
        changeQuantity(stock, newQty);
    }

    /**
     * Deducts quantity from a stock as a result of releasing
     * @param stock the stock record to be updated
     * @param quantity the quantity to be deducted from the stock
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void deductStockQuantity(Stock stock, double quantity) throws Exception {
        double newQty = stock.getQuantity() - quantity;
        changeQuantity(stock, newQty);
    }

    /**
     * Tags a stock as trashed
     * @param stock the stock to be trashed
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void trash(Stock stock) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE Stocks SET IsTrashed=1, TrashedAt=GETDATE(), UserIDTrashed=? WHERE id=?");
        ps.setString(1, ActiveUser.getUser().getId());
        ps.setString(2, stock.getId());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Restores a trashed stock
     * @param trash the stock to be trashed
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void restore(Stock trash) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE Stocks SET IsTrashed=0, UpdatedAt=GETDATE(), UserIDTrashed=? WHERE id=?");
        ps.setString(1, ActiveUser.getUser().getId());
        ps.setString(2, trash.getId());
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Implementation of the stock entry transaction
     * @param stock the stock entered
     * @param log entry log containing data such as quantity, source, etc.
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void stockEntry(Stock stock, StockEntryLog log) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO StockEntryLogs " +
                        "(StockID, Quantity, Source, Price, UserID, CreatedAt, UpdatedAt, id, RRNo) " +
                        "VALUES " +
                        "(?,?,?,?,?,GETDATE(),GETDATE(), ?, ?)");

        log.setId(Utility.generateRandomId());

        ps.setString(1, stock.getId());
        ps.setDouble(2, log.getQuantity());
        ps.setString(3, log.getSource());
        ps.setDouble(4, log.getPrice());
        ps.setString(5, ActiveUser.getUser().getId());
        ps.setString(6, log.getId());
        ps.setString(7, log.getRrNo());
        ps.executeUpdate();

        ps.close();

        addStockQuantity(stock, log.getQuantity());
    }

    /**
     * Update an existing StockEntryLog
     * @param log the StockEntryLog to be updated
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void updateStockEntry(StockEntryLog log) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE StockEntryLogs SET " +
                        "StockID=?, Quantity=?, Source=?, " +
                        "Price=?, UserID=?, RRNo=?, UpdatedAt=GETDATE() " +
                        "WHERE id=?");
        ps.setString(1, log.getStockID());
        ps.setDouble(2, log.getQuantity());
        ps.setString(3, log.getSource());
        ps.setDouble(4, log.getPrice());
        ps.setString(5, ActiveUser.getUser().getId());
        ps.setString(6, log.getId());
        ps.setString(7, log.getRrNo());

        ps.executeUpdate();

        ps.close();
    }

    /**
     * Get all the stock types
     * @return the List of StockTypes from the database
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<StockType> getTypes() throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM StockTypes ORDER BY StockType");
        ResultSet rs = ps.executeQuery();
        ArrayList<StockType> stockTypes = new ArrayList<>();

        while(rs.next()) {
            stockTypes.add(new StockType(
                    rs.getString("id"),
                    rs.getString("StockType"),
                    rs.getString("Units")));
        }

        rs.close();
        ps.close();

        return stockTypes;
    }

    /**
     * Retrieves a single StockType record
     * @param type the name of the StockType record to be retrieved
     * @return StockType object
     * @throws Exception obligatory from DB.getConnection()
     */
    public static StockType getStockType(String type) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM StockTypes WHERE StockType=?");
        ps.setString(1, type);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            StockType stock = new StockType(
                    rs.getString("id"),
                    rs.getString("StockType"),
                    rs.getString("Units")
            );

            rs.close();

            return stock;
        }
        rs.close();
        return null;
    }

    /**
     * Retrieves a single StockType record
     * @param id the identifier of the StockType record to be retrieved
     * @return StockType object
     * @throws Exception obligatory from DB.getConnection()
     */
    public static StockType getStockType(int id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM StockTypes WHERE id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            StockType stock = new StockType(
                    rs.getString("id"),
                    rs.getString("StockType"),
                    rs.getString("Units")
            );

            rs.close();

            return stock;
        }
        rs.close();
        return null;
    }

    /**
     * returns the number of stocks that are not trashed
     * @return int the number of rows in the Stocks table whose isTrashed is 0 or false
     * @throws Exception obligatory from DB.getConnection()
     */
    public static int countStocks() throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT COUNT(ID) AS 'count' FROM Stocks WHERE IsTrashed=0;");
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            return rs.getInt("count");
        }

        return 0;
    }

    /**
     * returns the number of stocks that are trashed
     * @return int the number of rows in the Stocks table whose isTrashed is 1 or true
     * @throws Exception obligatory from DB.getConnection()
     */
    public static int countTrashed() throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT COUNT(ID) AS 'count' FROM Stocks WHERE IsTrashed=1;");
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            return rs.getInt("count");
        }

        return 0;
    }

    /**
     * Get all the stocks which were trashed
     * @return List of trashed Stocks
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Stock> getTrashed() throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Stocks WHERE isTrashed=1 ORDER BY TrashedAt ASC");
        ResultSet rs = ps.executeQuery();

        ArrayList<Stock> trashedStocks = new ArrayList<>();

        while(rs.next()) {
            trashedStocks.add(new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            ));
        }

        rs.close();
        ps.close();

        return trashedStocks;
    }

    /**
     * Get the list of Critical Stocks
     * @return The List of stocks that are in at critical level
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Stock> getCritical() throws Exception {

        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Stocks WHERE Quantity <= Critical " +
                        "WHERE IsTrashed=0 ORDER BY Quantity");
        ps.setInt(1, CRITICAL);
        ResultSet rs = ps.executeQuery();

        ArrayList<Stock> criticalStocks = new ArrayList<>();

        while(rs.next()) {
            criticalStocks.add(new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            ));
        }

        rs.close();
        ps.close();

        return criticalStocks;
    }

    /**
     * Get the list of Critical Stocks with pagination
     * @param limit row per page
     * @param offset page
     * @return The List of stocks that are in at critical level
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Stock> getCritical(int limit, int offset) throws Exception {

        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Stocks WHERE Quantity <= Critical " +
                        "AND IsTrashed=0 ORDER BY Quantity "+
                        "OFFSET ? ROWS " +
                        "FETCH NEXT ? ROWS ONLY");
        ps.setInt(1, offset);
        ps.setInt(2, limit);
        ResultSet rs = ps.executeQuery();

        ArrayList<Stock> criticalStocks = new ArrayList<>();

        while(rs.next()) {
            criticalStocks.add(new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")

            ));
        }

        rs.close();
        ps.close();

        return criticalStocks;
    }

    /**
     * Count the number of Stocks which are at critical level
     * @return The number of Stocks in critical level.
     * @throws Exception obligatory from DB.getConnection()
     */
    public static int countCritical() throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT COUNT(ID) AS 'count' FROM Stocks WHERE IsTrashed=0 AND Quantity<=Critical;");
        ResultSet rs = ps.executeQuery();

        int count = 0;

        if(rs.next()) {
            count = rs.getInt("count");
        }
        rs.close();
        ps.close();

        return count;
    }

    /**
     * Get the entry logs of a given Stock
     * @param stock the stock to get the entries
     * @return The List of StockEntryLogs for this particular Stock
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<StockEntryLog> getEntryLogs(Stock stock) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM StockEntryLogs WHERE StockID=? ORDER BY CreatedAt DESC;");
        ps.setString(1, stock.getId());

        ResultSet rs = ps.executeQuery();

        ArrayList<StockEntryLog> entryLogs = new ArrayList<>();

        while(rs.next()) {
            entryLogs.add(
                    new StockEntryLog(
                            rs.getString("id"),
                            rs.getString("StockID"),
                            rs.getInt("Quantity"),
                            rs.getString("Source"),
                            rs.getDouble("Price"),
                            rs.getString("UserID"),
                            rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                            rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                            rs.getString("RRNo")
                    )
            );
        }

        rs.close();
        ps.close();

        return entryLogs;
    }

    /**
     * Get the releases of a given Stock
     * @param stock the stock to get the entries
     * @return The List of Releasing for this particular Stock
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Releasing> getReleasedStocks(Stock stock) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Releasing WHERE StockID=? ORDER BY UpdatedAt DESC;");
        ps.setString(1, stock.getId());
        ResultSet rs = ps.executeQuery();

        ArrayList<Releasing> releasedStocks = new ArrayList<>();

        while(rs.next()) {
            Releasing  releasing = new Releasing(
                    rs.getString("id"),
                    rs.getString("StockID"),
                    rs.getString("MIRSID"),
                    rs.getInt("Quantity"),
                    rs.getDouble("Price"),
                    rs.getString("UserID"),
                    rs.getString("Status"),
                    rs.getString("WorkOrderNo"),
                    rs.getString("mct_no")
            );
            releasing.setCreatedAt(rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null);
            releasing.setUpdatedAt(rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null);
            releasedStocks.add(releasing);
        }

        rs.close();
        ps.close();

        return releasedStocks;
    }

    /**
     * Get the current stock inventories. Used in InventoryReport.
     * @param limit limit of rows per page
     * @param offset page number in the result set
     * @return The list of current stocks for this particular date
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Stock> getInventory(int limit, int offset) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Stocks "+
                        "WHERE IsTrashed=0 " +
                        "ORDER BY StockName ASC, Quantity DESC "+
                        "OFFSET ? ROWS " +
                        "FETCH NEXT ? ROWS ONLY");

        ps.setInt(1, offset);
        ps.setInt(2, limit);

        ResultSet rs = ps.executeQuery();
        ArrayList<Stock> stocks = new ArrayList<>();

        while(rs.next()) {
            stocks.add(new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            ));
        }

        rs.close();
        ps.close();
        return stocks;
    }

    /**
     * Get the current stock inventories. Used in InventoryReport.
     * @return The list of current stocks for this particular date
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Stock> getInventory() throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Stocks " +
                        "WHERE IsTrashed=0 "+
                        "ORDER BY StockName ASC, Quantity DESC");

        ResultSet rs = ps.executeQuery();
        ArrayList<Stock> stocks = new ArrayList<>();

        while(rs.next()) {
            stocks.add(new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            ));
        }

        rs.close();
        ps.close();
        return stocks;
    }

    /**
     * Get the stock entries of a given period. Used in StockEntryReport.
     * @param from start date
     * @param to end date
     * @return The list of stocks for this particular period
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Stock> getStockEntries(LocalDate from, LocalDate to, String source) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT Stocks.*, StockEntryLogs.id as entryID, StockEntryLogs.Price as entryPrice, StockEntryLogs.Quantity as entryQuantity, StockEntryLogs.Source as entrySource, " +
                        "StockEntryLogs.CreatedAt as entryCreatedAt, StockEntryLogs.UpdatedAt as entryUpdatedAt, StockEntryLogs.UserID as entryUserID, RRNo " +
                        "FROM Stocks LEFT JOIN StockEntryLogs " +
                        "ON StockEntryLogs.StockID=Stocks.id " +
                        "WHERE IsTrashed=0 AND StockEntryLogs.CreatedAt BETWEEN ? AND ? AND StockEntryLogs.Source=? " +
                        "ORDER BY StockEntryLogs.CreatedAt ASC, Stocks.StockName ASC");

        ps.setDate(1, Date.valueOf(from));
        ps.setDate(2, Date.valueOf(to));
        ps.setString(3, source);

        ResultSet rs = ps.executeQuery();
        ArrayList<Stock> stocks = new ArrayList<>();

        while(rs.next()) {
            Stock stock = new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            );
            StockEntryLog log = new StockEntryLog();
            log.setId(rs.getString("entryID"));
            log.setStockID(rs.getString("id"));
            log.setSource(rs.getString("entrySource"));
            log.setPrice(rs.getDouble("entryPrice"));
            log.setQuantity(rs.getInt("entryQuantity"));
            log.setUserID(rs.getString("entryUserID"));
            log.setRrNo(rs.getString("RRNo"));
            log.setCreatedAt(rs.getTimestamp("entryCreatedAt")!=null ? rs.getTimestamp("entryCreatedAt").toLocalDateTime() : null);
            log.setUpdatedAt(rs.getTimestamp("entryUpdatedAt")!=null ? rs.getTimestamp("entryUpdatedAt").toLocalDateTime() : null);
            stock.setEntryLog(log);
            stocks.add(stock);
        }

        rs.close();
        ps.close();
        return stocks;
    }
    /**
     * Get the stock entries of a given period per page. Used in StockEntryReport pagination.
     * @param from start date
     * @param to end date
     * @param limit limit of rows per page
     * @param offset page number in the result set
     * @return The list of stocks for this particular period
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Stock> getStockEntries(LocalDate from, LocalDate to, int limit, int offset, String source) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT Stocks.*, StockEntryLogs.id as entryID, StockEntryLogs.Price as entryPrice, StockEntryLogs.Quantity as entryQuantity, StockEntryLogs.Source as entrySource, " +
                        "StockEntryLogs.CreatedAt as entryCreatedAt, StockEntryLogs.UpdatedAt as entryUpdatedAt, StockEntryLogs.UserID as entryUserID, RRNo " +
                        "FROM Stocks LEFT JOIN StockEntryLogs " +
                        "ON StockEntryLogs.StockID=Stocks.id " +
                        "WHERE IsTrashed=0 AND StockEntryLogs.CreatedAt BETWEEN ? AND ? AND StockEntryLogs.Source=? " +
                        "ORDER BY StockEntryLogs.CreatedAt ASC, Stocks.StockName ASC "+
                        "OFFSET ? ROWS " +
                        "FETCH NEXT ? ROWS ONLY");

        ps.setDate(1, Date.valueOf(from));
        ps.setDate(2, Date.valueOf(to));
        ps.setInt(3, offset);
        ps.setInt(4, limit);
        ps.setString(5, source);

        ResultSet rs = ps.executeQuery();
        ArrayList<Stock> stocks = new ArrayList<>();

        while(rs.next()) {
            Stock stock = new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            );
            StockEntryLog log = new StockEntryLog();
            log.setId(rs.getString("entryID"));
            log.setStockID(rs.getString("id"));
            log.setSource(rs.getString("entrySource"));
            log.setPrice(rs.getDouble("entryPrice"));
            log.setQuantity(rs.getInt("entryQuantity"));
            log.setUserID(rs.getString("entryUserID"));
            log.setRrNo(rs.getString("RRNo"));
            log.setCreatedAt(rs.getTimestamp("entryCreatedAt")!=null ? rs.getTimestamp("entryCreatedAt").toLocalDateTime() : null);
            log.setUpdatedAt(rs.getTimestamp("entryUpdatedAt")!=null ? rs.getTimestamp("entryUpdatedAt").toLocalDateTime() : null);
            stock.setEntryLog(log);
            stocks.add(stock);
        }

        rs.close();
        ps.close();
        return stocks;
    }

    /**
     * Get the released stocks of a given period. Used in StockLiquidationReport.
     * @param from start date
     * @param to end date
     * @return The list of released stocks for this particular period
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Stock> getReleasedStocks(LocalDate from, LocalDate to) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT Stocks.*, Releasing.id as releasedID, Releasing.MIRSID as releasedMIRSID, Releasing.Quantity as releasedQuantity, " +
                        "Releasing.Price as releasedPrice, Releasing.UserID as releasedUserID, Releasing.Status as releasedStatus, " +
                        "Releasing.CreatedAt as releasedCreatedAt, Releasing.UpdatedAt as releasedUpdatedAt " +
                        "FROM Stocks LEFT JOIN Releasing " +
                        "ON Releasing.StockID=Stocks.id " +
                        "WHERE IsTrashed=0 AND Releasing.CreatedAt BETWEEN ? AND ? " +
                        "ORDER BY Releasing.UpdatedAt ASC, Stocks.StockName ASC");

        ps.setDate(1, Date.valueOf(from));
        ps.setDate(2, Date.valueOf(to));

        ResultSet rs = ps.executeQuery();
        ArrayList<Stock> stocks = new ArrayList<>();

        while(rs.next()) {
            Stock stock = new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("Price"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            );
            Releasing log = new Releasing();
            log.setId(rs.getString("releasedID"));
            log.setStockID(rs.getString("id"));
            log.setMirsID(rs.getString("releasedMIRSID"));
            log.setStatus(rs.getString("releasedStatus"));
            log.setPrice(rs.getDouble("releasedPrice"));
            log.setQuantity(rs.getInt("releasedQuantity"));
            log.setUserID(rs.getString("releasedUserID"));
            log.setCreatedAt(rs.getTimestamp("releasedCreatedAt")!=null ? rs.getTimestamp("releasedCreatedAt").toLocalDateTime() : null);
            log.setUpdatedAt(rs.getTimestamp("releasedUpdatedAt")!=null ? rs.getTimestamp("releasedUpdatedAt").toLocalDateTime() : null);
            stock.setReleasing(log);
            stocks.add(stock);
        }

        rs.close();
        ps.close();
        return stocks;
    }
    /**
     * Get the released stocks of a given period. Used in StockLiquidationReport pagination.
     * @param from start date
     * @param to end date
     * @param limit limit of rows per page
     * @param offset page number in the result set
     * @return The list of released stocks for this particular period
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Stock> getReleasedStocks(LocalDate from, LocalDate to, int limit, int offset) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT Stocks.*, Releasing.id as releasedID, Releasing.MIRSID as releasedMIRSID, Releasing.Quantity as releasedQuantity, " +
                        "Releasing.Price as releasedPrice, Releasing.UserID as releasedUserID, Releasing.Status as releasedStatus, " +
                        "Releasing.CreatedAt as releasedCreatedAt, Releasing.UpdatedAt as releasedUpdatedAt " +
                        "FROM Stocks LEFT JOIN Releasing " +
                        "ON Releasing.StockID=Stocks.id " +
                        "WHERE IsTrashed=0 AND Releasing.UpdatedAt BETWEEN ? AND ? AND Releasing.Status='Approved' " +
                        "ORDER BY Releasing.UpdatedAt ASC, Stocks.StockName ASC "+
                        "OFFSET ? ROWS " +
                        "FETCH NEXT ? ROWS ONLY");

        ps.setDate(1, Date.valueOf(from));
        ps.setDate(2, Date.valueOf(to));
        ps.setInt(3, offset);
        ps.setInt(4, limit);

        ResultSet rs = ps.executeQuery();
        ArrayList<Stock> stocks = new ArrayList<>();

        while(rs.next()) {
            Stock stock = new Stock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getString("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("releasedQuantity"),
                    rs.getInt("Critical"),
                    rs.getDouble("releasedPrice"),
                    rs.getString("NEACode"),
                    rs.getBoolean("IsTrashed"),
                    rs.getString("Comments"),
                    rs.getTimestamp("releasedCreatedAt")!=null ? rs.getTimestamp("releasedCreatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("releasedUpdatedAt")!=null ? rs.getTimestamp("releasedUpdatedAt").toLocalDateTime() : null,
                    rs.getTimestamp("TrashedAt")!=null ? rs.getTimestamp("TrashedAt").toLocalDateTime() : null,
                    rs.getString("UserIDCreated"),
                    rs.getString("UserIDUpdated"),
                    rs.getString("UserIDTrashed"),
                    rs.getString("LocalCode"),
                    rs.getString("AcctgCode"),
                    rs.getBoolean("Individualized")
            );
            stocks.add(stock);
        }

        rs.close();
        ps.close();
        return stocks;
    }

    /**
     * Return the pending quantity of a MIRSItem
     * @param stock
     * @return
     * @throws Exception obligatory from DB.getConnection()
     */
    public static double countPendingRequest(Stock stock) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT SUM(mi.Quantity) AS 'pending' FROM MIRSItems mi " +
                        "LEFT JOIN MIRS m ON m.id = mi.MIRSID LEFT JOIN Stocks s ON s.id = mi.StockID " +
                        "WHERE (m.Status = '"+Utility.PENDING+"' OR m.Status = '"+Utility.RELEASING+"') AND s.Description=?");
        ps.setString(1, stock.getDescription());
        ResultSet rs = ps.executeQuery();

        double count = 0;

        if(rs.next()) {
            count = rs.getDouble("pending");
        }

        double unavailable = StockDAO.countReleasingUnavailable(stock);
        return count-unavailable;
    }

    public static double countReleasingUnavailable(Stock stock) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT SUM(r.Quantity) AS 'Unavailable' FROM Releasing r " +
                        "INNER JOIN Stocks s ON s.id = r.StockID " +
                        "WHERE r.MIRSID IN (SELECT m.id FROM MIRS m WHERE m.Status='releasing') " +
                        "AND s.Description = ?"
        );

        ps.setString(1, stock.getDescription());

        ResultSet rs = ps.executeQuery();

        double count = 0;

        if(rs.next()) {
            count = rs.getDouble("unavailable");
        }

        return count;
    }

    public static double countAvailable(Stock stock) throws Exception {
        double pending = countPendingRequest(stock);

        return stock.getQuantity()-pending;
    }

    /**
     * Get a list of Stock Descriptions based on a search key
     * @param key
     * @return
     */
    public static List<StockDescription> searchDescription(String key) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT (SELECT TOP 1 id FROM Stocks s2 WHERE s2.Description=s.Description) AS id, Description, SUM(Quantity) as Qty\n" +
                        "FROM Stocks s \n" +
                        "WHERE s.Description LIKE ? AND s.Quantity > 0\n" +
                        "GROUP BY Description\n" +
                        "ORDER BY Description;");

        ps.setString(1, "%" + key + "%");

        List<StockDescription> stockDescriptions = new ArrayList();

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            stockDescriptions.add(new StockDescription(
                    rs.getString("id"),
                    rs.getString("Description"),
                    rs.getInt("Qty")
            ));
        }

        rs.close();
        ps.close();
        return stockDescriptions;
    }




    public static boolean hasMultiple(String description) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT COUNT(id) as count FROM Stocks WHERE Description=?");
        ps.setString(1, description);
        ResultSet rs = ps.executeQuery();
        rs.next();

        return rs.getInt("count")>1;
    }

    public static List<SlimStock> getByDescription(String description) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT id, StockName, Brand, Model, Description, Price, Unit, Quantity FROM Stocks " +
                        "WHERE Description=? and Quantity > 0 and PRICE > 0 ORDER BY Brand");

        ps.setString(1, description);

        ResultSet rs = ps.executeQuery();

        ArrayList<SlimStock> stocks = new ArrayList<>();
        while(rs.next()) {
            SlimStock stock = new SlimStock(
                    rs.getString("id"),
                    rs.getString("StockName"),
                    rs.getString("Model"),
                    rs.getString("Brand"));
            stock.setDescription(rs.getString("Description"));
            stock.setPrice(rs.getDouble("Price"));
            stock.setUnit(rs.getString("Unit"));
            stock.setQuantity(rs.getInt("Quantity"));
            stocks.add(stock);
        }

        rs.close();
        ps.close();

        return stocks;
    }

    public static List<SummaryCharges> getSummaryCharges(String to, String from) throws SQLException, ClassNotFoundException {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT a.id, NEACode, LocalCode, AcctgCode, description, a.price, a.Quantity, \n" +
                        "COALESCE((SELECT SUM(Quantity) FROM Releasing WHERE Releasing.StockID=a.id AND UpdatedAt >=? AND UpdatedAt <= ? GROUP BY Releasing.StockID),0) AS Issued, \n" +
                        "COALESCE((SELECT SUM(QtyAccepted) FROM ReceivingItem INNER JOIN Receiving ON Receiving.RRNo = ReceivingItem.RRNo WHERE ReceivingItem.StockID=a.id AND Date >=? AND Date <= ? GROUP BY ReceivingItem.StockID),0) AS Received,\n" +
                        "COALESCE((SELECT SUM(quantity) FROM MRTItem INNER JOIN MRT ON MRT.id=MRTItem.mrt_id WHERE MRTItem.releasing_id=a.id AND dateOfReturned >=? AND dateOfReturned <= ? GROUP BY MRTItem.releasing_id),0) AS Returned, \n" +
                        "a.Quantity\n" +
                        "+COALESCE((SELECT SUM(QtyAccepted) FROM ReceivingItem INNER JOIN Receiving ON Receiving.RRNo = ReceivingItem.RRNo WHERE ReceivingItem.StockID=a.id AND Date >=? AND Date <= ? GROUP BY ReceivingItem.StockID),0)\n" +
                        "+COALESCE((SELECT SUM(quantity) FROM MRTItem INNER JOIN MRT ON MRT.id=MRTItem.mrt_id WHERE MRTItem.releasing_id=a.id AND dateOfReturned >=? AND dateOfReturned <= ? GROUP BY MRTItem.releasing_id),0)\n" +
                        "-COALESCE((SELECT SUM(Quantity) FROM Releasing WHERE Releasing.StockID=a.id AND UpdatedAt >=? AND UpdatedAt <= ? GROUP BY Releasing.StockID),0)\n" +
                        "AS Balance\n" +
                        "FROM stocks a left join ReceivingItem b on a.id=b.StockID LEFT JOIN Releasing c ON a.id=c.StockID left join MRTItem d ON a.id=d.releasing_id ORDER BY a.description ASC");

        ps.setString(1, to);
        ps.setString(2, from);

        ps.setString(3, to);
        ps.setString(4, from);

        ps.setString(5, to);
        ps.setString(6, from);

        ps.setString(7, to);
        ps.setString(8, from);

        ps.setString(9, to);
        ps.setString(10, from);

        ps.setString(11, to);
        ps.setString(12, from);

        ResultSet rs = ps.executeQuery();

        ArrayList<SummaryCharges> list = new ArrayList<>();
        while(rs.next()) {
            SummaryCharges data = new SummaryCharges(
                    rs.getString("id"),
                    rs.getString("neacode"),
                    rs.getString("localcode"),
                    rs.getString("acctgcode"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getInt("quantity"),
                    rs.getInt("issued"),
                    rs.getInt("received"),
                    rs.getInt("returned"),
                    rs.getInt("balance"));

            list.add(data);
        }

        rs.close();
        ps.close();

        return list;
    }
}
