package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.SlimStock;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.boheco1.dev.integratedaccountingsystem.objects.StockEntryLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StockDAO {
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
        ps.setInt(3, stock.getSerialNumber());

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
                    rs.getInt("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate").toLocalDate(),
                    rs.getDate("ValidityDate").toLocalDate(),
                    rs.getInt("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
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
        ps.setInt(3, stock.getSerialNumber());

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
     * Retrieves a list of SlimStocks as a search result based on a search Key
     * @param key The search key
     * @return A list of SlimStock that qualifies with the search key
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<SlimStock> search(String key) throws Exception  {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "Select TOP 50 id, StockName, Brand, Model FROM Stocks " +
                        "WHERE (StockName LIKE '%?%' OR Brand LIKE '%?%' OR Model LIKE '%?%' ) " +
                        "AND IsTrashed=0 ORDER BY StockName");
        ps.setString(1, key);
        ps.setString(2, key);
        ps.setString(3, key);

        ResultSet rs = ps.executeQuery();

        ArrayList<SlimStock> stocks = new ArrayList<>();
        while(rs.next()) {
            stocks.add(new SlimStock(
                    rs.getInt("id"),
                    rs.getString("StockName"),
                    rs.getString("Model"),
                    rs.getString("Brand")
            ));
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
                    rs.getInt("SerialNumber"),
                    rs.getString("Brand"),
                    rs.getString("Model"),
                    rs.getDate("ManufacturingDate").toLocalDate(),
                    rs.getDate("ValidityDate").toLocalDate(),
                    rs.getInt("TypeID"),
                    rs.getString("Unit"),
                    rs.getInt("Quantity"),
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
        if(rs.first()) {
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
}
