package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.*;
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
                        "UserIDCreated) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,GETDATE(),?)", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, stock.getStockName());
        ps.setString(2, stock.getDescription());
        ps.setString(3, stock.getSerialNumber());

        ps.setString(4, stock.getBrand());
        ps.setString(5, stock.getModel());
        ps.setDate(6, stock.getManufacturingDate()!=null ? Date.valueOf(stock.getManufacturingDate()) : null);

        ps.setDate(7, stock.getValidityDate()!=null ? Date.valueOf(stock.getValidityDate()) : null);
        ps.setInt(8, stock.getTypeID());
        ps.setString(9, stock.getUnit());

        ps.setInt(10, stock.getQuantity());
        ps.setDouble(11, stock.getPrice());
        ps.setString(12, stock.getNeaCode());

        ps.setBoolean(13, stock.isTrashed());
        ps.setString(14, stock.getComments());
        ps.setInt(15, stock.getUserIDCreated());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if(rs.next()) {
            stock.setId(rs.getInt(1));
        }

        rs.close();
        ps.close();
    }

    /**
     * Retrieves a single Stock record
     * @param id the identifier of the Stock record to be retrieved
     * @return Stock object
     * @throws Exception obligatory from DB.getConnection()
     */
    public static Stock get(int id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Stocks WHERE id=?");
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            Stock stock = new Stock(
                    rs.getInt("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getInt("TypeID"),
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
                    rs.getInt("UserIDCreated"),
                    rs.getInt("UserIDUpdated"),
                    rs.getInt("UserIDTrashed")
            );

            rs.close();

            return stock;
        }
        rs.close();
        return null;
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
                        "Comments=?, UpdatedAt=GETDATE(), UserIDCreated=? " +
                        "WHERE id=?");
        ps.setString(1, stock.getStockName());
        ps.setString(2, stock.getDescription());
        ps.setString(3, stock.getSerialNumber());

        ps.setString(4, stock.getBrand());
        ps.setString(5, stock.getModel());
        ps.setDate(6, Date.valueOf(stock.getManufacturingDate()));

        ps.setDate(7, Date.valueOf(stock.getValidityDate()));
        ps.setInt(8,stock.getTypeID());
        ps.setString(9, stock.getUnit());

        ps.setInt(10, stock.getQuantity());
        ps.setDouble(11, stock.getPrice());
        ps.setString(12, stock.getNeaCode());

        ps.setString(13, stock.getComments());
        ps.setInt(14, ActiveUser.getUser().getId());
        ps.setInt(15, stock.getId());

        ps.executeUpdate();

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
                        "Comments=?, UpdatedAt=GETDATE(), UserIDCreated=? " +
                        "WHERE id=?");
        ps.setString(1, stock.getStockName());
        ps.setString(2, stock.getDescription());
        ps.setString(3, stock.getSerialNumber());

        ps.setString(4, stock.getBrand());
        ps.setString(5, stock.getModel());

        if (stock.getManufacturingDate() != null) {
            ps.setDate(6, Date.valueOf(stock.getManufacturingDate()));
        }else{
            ps.setDate(6, null);
        }

        if (stock.getValidityDate() != null) {
            ps.setDate(7, Date.valueOf(stock.getValidityDate()));
        }else{
            ps.setDate(7, null);
        }

        ps.setInt(8,stock.getTypeID());
        ps.setString(9, stock.getUnit());

        ps.setDouble(10, stock.getPrice());
        ps.setString(11, stock.getNeaCode());

        ps.setString(12, stock.getComments());
        ps.setInt(13, ActiveUser.getUser().getId());
        ps.setInt(14, stock.getId());

        ps.executeUpdate();

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
                "Select TOP 50 id, StockName, Brand, Model, Description, Price, Unit, Quantity FROM Stocks " +
                        "WHERE (StockName LIKE ? OR Brand LIKE ? OR Model LIKE ? ) " +
                        "AND IsTrashed=? ORDER BY StockName");
        ps.setString(1, "%" + key + "%");
        ps.setString(2, "%" + key + "%");
        ps.setString(3, "%" + key + "%");
        ps.setInt(4, trashed);

        ResultSet rs = ps.executeQuery();

        ArrayList<SlimStock> stocks = new ArrayList<>();
        while(rs.next()) {
            SlimStock stock = new SlimStock(
                    rs.getInt("id"),
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
                        "ORDER BY StockName " +
                        "OFFSET ? ROWS " +
                        "FETCH NEXT ? ROWS ONLY");
        ps.setInt(1, trashed);
        ps.setInt(2, offset);
        ps.setInt(3, limit);

        ResultSet rs = ps.executeQuery();

        ArrayList<SlimStock> stocks = new ArrayList<>();

        while(rs.next()) {
            SlimStock stock = new SlimStock(
                    rs.getInt("id"),
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
                        "ORDER BY UpdatedAt, StockName " +
                        "OFFSET ? ROWS " +
                        "FETCH NEXT ? ROWS ONLY");
        ps.setInt(1, offset);
        ps.setInt(2, limit);

        ResultSet rs = ps.executeQuery();

        ArrayList<Stock> stocks = new ArrayList<>();

        while(rs.next()) {
            stocks.add(new Stock(
                    rs.getInt("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getInt("TypeID"),
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
                    rs.getInt("UserIDCreated"),
                    rs.getInt("UserIDUpdated"),
                    rs.getInt("UserIDTrashed")
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
    private static void changeQuantity(Stock stock, int quantity) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE Stocks SET quantity=?, UpdatedAt=GETDATE(), UserIDUpdated=? " +
                        "WHERE id = ?");
        ps.setInt(1, quantity);
        ps.setInt(2, ActiveUser.getUser().getId());
        ps.setInt(3, stock.getId());

        ps.executeUpdate();

        ps.close();
    }

    /**
     * Add quantity to a stock as a result of stock entry
     * @param stock the stock record to be updated
     * @param quantity the quantity to be added to the stock
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void addStockQuantity(Stock stock, int quantity) throws Exception {
        int newQty = stock.getQuantity() + quantity;
        changeQuantity(stock, newQty);
    }

    /**
     * Deducts quantity from a stock as a result of releasing
     * @param stock the stock record to be updated
     * @param quantity the quantity to be deducted from the stock
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void deductStockQuantity(Stock stock, int quantity) throws Exception {
        int newQty = stock.getQuantity() - quantity;
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
        ps.setInt(1, ActiveUser.getUser().getId());
        ps.setInt(2, stock.getId());
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
        ps.setInt(1, ActiveUser.getUser().getId());
        ps.setInt(2, trash.getId());
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
                        "(StockID, Quantity, Source, Price, UserID, CreatedAt, UpdatedAt) " +
                        "VALUES " +
                        "(?,?,?,?,?,GETDATE(),GETDATE())", Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, stock.getId());
        ps.setInt(2, log.getQuantity());
        ps.setString(3, log.getSource());
        ps.setDouble(4, log.getPrice());
        ps.setInt(5, ActiveUser.getUser().getId());
        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if(rs.next()) {
            int id = rs.getInt(1);
            log.setId(id);
        }
        rs.close();
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
                        "Price=?, UserID=?, UpdatedAt=GETDATE() " +
                        "WHERE id=?");
        ps.setInt(1, log.getStockID());
        ps.setInt(2, log.getQuantity());
        ps.setString(3, log.getSource());
        ps.setDouble(4, log.getPrice());
        ps.setInt(5, ActiveUser.getUser().getId());
        ps.setInt(6, log.getId());

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
            stockTypes.add(new StockType(rs.getInt("id"), rs.getString("StockType")));
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
                    rs.getInt("id"),
                    rs.getString("StockType")
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
                    rs.getInt("id"),
                    rs.getString("StockType")
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
                    rs.getInt("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getInt("TypeID"),
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
                    rs.getInt("UserIDCreated"),
                    rs.getInt("UserIDUpdated"),
                    rs.getInt("UserIDTrashed")
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
                "SELECT * FROM Stocks WHERE Quantity < Critical ORDER BY Quantity");
        ps.setInt(1, CRITICAL);
        ResultSet rs = ps.executeQuery();

        ArrayList<Stock> criticalStocks = new ArrayList<>();

        while(rs.next()) {
            criticalStocks.add(new Stock(
                    rs.getInt("id"),
                    rs.getString("StockName"),
                    rs.getString("Description"),
                    rs.getString("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate")!=null ? rs.getDate("ManufacturingDate").toLocalDate() : null,
                    rs.getDate("ValidityDate")!=null ? rs.getDate("ValidityDate").toLocalDate() : null,
                    rs.getInt("TypeID"),
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
                    rs.getInt("UserIDCreated"),
                    rs.getInt("UserIDUpdated"),
                    rs.getInt("UserIDTrashed")
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
        ps.setInt(1, CRITICAL);
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
                "SELECT * FROM StockEntryLogs WHERE StockID=?;");
        ps.setInt(1, stock.getId());

        ResultSet rs = ps.executeQuery();

        ArrayList<StockEntryLog> entryLogs = new ArrayList<>();

        while(rs.next()) {
            entryLogs.add(
                    new StockEntryLog(
                            rs.getInt("id"),
                            rs.getInt("StockID"),
                            rs.getInt("Quantity"),
                            rs.getString("Source"),
                            rs.getDouble("Price"),
                            rs.getInt("UserID"),
                            rs.getTimestamp("CreatedAt").toLocalDateTime(),
                            rs.getTimestamp("UpdatedAt").toLocalDateTime()
                    )
            );
        }

        rs.close();
        ps.close();

        return entryLogs;
    }

}
