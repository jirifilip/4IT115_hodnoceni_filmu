/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import db.Database;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Stream;
import javafx.scene.image.Image;
import utils.Crypto;

/**
 * Entita uživatel
 * 
 * @author Jirka_
 */
public class User extends Model {
    
    private int id;
    private String email;
    private String username;
    private String password;
    private String profileDescription;
    private String profilePhotoUrl;
    private String registrationDate;
    private String lastLogin;
    private String admin;
    
    private final String placeholderURL = "/resources/placeholder.jpg";

    /**
     * Konstruktor pro vytvoření uživatele při přenosu z databáze.
     * 
     * @param id
     * @param username
     * @param email
     * @param password
     * @param profileDescription
     * @param profilePhotoUrl
     * @param registrationDate
     * @param lastLogin
     * @param admin
     */
    public User(int id, String username, String email, String password, String profileDescription, String profilePhotoUrl, String registrationDate, String lastLogin, String admin) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.password = password;
        this.profileDescription = profileDescription;
        this.profilePhotoUrl = profilePhotoUrl;
        this.registrationDate = registrationDate;
        this.lastLogin = lastLogin;
        this.admin = admin;
    }
    
    /**
     *
     * Kontruktor pro vytváření uživatele při vytvoření nového uživatele
     * v databázi
     * 
     * @param username
     * @param email
     * @param password
     */
    public User(String username, String email, String password) {
        
        this.username = username;
        this.email = email;
        this.password = Crypto.hashSHA256(password);
        
        Date date = new Date();
        
        this.registrationDate = new Timestamp(date.getTime()).toString();
        
        ArrayList<String> paramList = new ArrayList<>();
        paramList.add(this.username);
        paramList.add(this.email);
        paramList.add(this.password);
        paramList.add(this.registrationDate);
        
        Database
            .getInstance()
            .executeAction(
                    "insert into user(username, email, password, registered_at)"
                    + "values(?, ?, ?, ?)" , paramList);
        
        this.admin = "0";
    }
    
    /**
     * Na základě jména a hesla přinese uživatele
     * @param username
     * @param password
     * @return uživatele
     */
    public static User fetchUser(String username, String password) {
        return Database.getInstance().fetchUser(username, password);
    }
    
    /**
     * Na základě id přinese uživatele
     * 
     * @param userId
     * @return uživatel
     */
    public static User fetchUser(int userId) {
        PreparedStatement statement = null;
        User user = null;

        try (Connection connection = Database.getConnection()){
            ResultSet rs = null;
            
            statement = connection.prepareStatement(
                "SELECT * FROM user where id_user = ?"
            );
            
            statement.setString(1, String.valueOf(userId));
            
            rs = statement.executeQuery();
            
            if (rs.next()) {
                int id_ = rs.getInt(1);
                String username_ = rs.getString(2);
                String email_ = rs.getString(3);
                String password_ = rs.getString(4);
                String profileDesc = rs.getString(5);
                String profilePhoto = rs.getString(6);
                String registeredAt = rs.getString(7);
                String lastLogin = rs.getString(8);
                String admin = rs.getString(9);
                
                user = new User(id_, username_, email_, password_, profileDesc, profilePhoto, registeredAt, lastLogin, admin);
            }

            rs.close();
                

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return user;
    }
    
    /**
     * Znovu načtení konkrétní instance z databáze
     */
    public void reload() {
        PreparedStatement statement = null;

        try (Connection connection = Database.getConnection()){
            ResultSet rs = null;
            
            statement = connection.prepareStatement(
                "SELECT * FROM user where id_user=?"
            );
            
            statement.setString(1, String.valueOf(id));
            
            rs = statement.executeQuery();
            
            if (rs.next()) {
                this.profileDescription = rs.getString(5);
                this.profilePhotoUrl = rs.getString(6);
                this.registrationDate = rs.getString(7);
                this.registrationDate = rs.getString(8);
                this.admin = rs.getString(9);
                
                
            }
            

            rs.close();
                

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Metoda pro aktualizaci údajů v databázi na základě
     * změněných atributů instance
     */
    public void update() {
        ArrayList<String> paramList = new ArrayList<>();
        paramList.add(this.username);
        paramList.add(this.email);
        paramList.add(this.password);
        paramList.add(this.profileDescription);
        paramList.add(this.profilePhotoUrl);
        paramList.add(this.lastLogin);
        paramList.add(this.registrationDate);
        paramList.add(this.admin);
        paramList.add(String.valueOf(this.id));
        
        Database
            .getInstance()
            .executeAction(
                    "update user "
                    + "set username=?, "
                    + "email=?, "
                    + "password=?, "
                    + "profile_desc=?, "
                    + "profile_photo=?, "
                    + "last_login=?, "
                    + "registered_at=?, "
                    + "admin=? "
                    + "where id_user=? "
                    , paramList);
        
    }
    
    /**
     * Metoda pro zjištění, zda uživatel hodnotil filmovou stránku
     * 
     * @param movieSite
     * @return jestli už uživatel hodnotil filmovou stránku
     */
    public boolean hasRatedMovieSite(MovieSite movieSite) {
        boolean result = false;
        
        try (Connection conn = Database.getConnection()){
            PreparedStatement statement = null;
            ResultSet rs = null;
            
            statement = conn.prepareStatement("select * from movie_page_rating "
                    + "where movie_page_id=? "
                    + "and user_id=?;");
            
            statement.setString(1, String.valueOf(movieSite.getId()));
            statement.setString(2, String.valueOf(this.id));
            
            rs = statement.executeQuery();
            
            while (rs.next()) {
                result = true;
            }
            
            statement.close();
            conn.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    /**
     * Statická metoda pro přinesení všech uživatelů.
     * @return seznam uživatelů
     */
    public static ArrayList<User> fetchAll() {
        ArrayList<User> users = new ArrayList<>();
        
        try (Connection conn = Database.getConnection()){
            PreparedStatement statement = null;
            ResultSet rs = null;
            
            statement = conn.prepareStatement("select * from user");
            
            rs = statement.executeQuery();
            
            while (rs.next()) {
                
                int id_ = rs.getInt(1);
                String username_ = rs.getString(2);
                String email_ = rs.getString(3);
                String password_ = rs.getString(4);
                String profileDesc = rs.getString(5);
                String profilePhoto = rs.getString(6);
                String registeredAt = rs.getString(7);
                String lastLogin = rs.getString(8);
                String admin = rs.getString(9);
                
                User user = new User(id_, username_, email_, password_, profileDesc, profilePhoto, registeredAt, lastLogin, admin);
                
                
                users.add(user);
            }
            
            statement.close();
            conn.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * metoda pro získání profilového obrázku uživatele
     * @param w
     * @param h
     * @return profilový obrázek uživatele
     */
    public Image getProfileImage(double w, double h) {
        File file = new File(System.getProperty("user.dir") + "/src/resources/" + getUsername() + ".jpg");
        String url = file.exists() ? "/resources/" + getUsername() + ".jpg" : placeholderURL;
        
        return new Image(User.class.getResourceAsStream(url), w, h, true, true);
    }
    
    /**
     * Statická metoda pro vyhledání uživatele na základě zadaného
     * textového řetězce
     * 
     * @param searchString
     * @return list uživatelů odpovídající zadanému parametru
     */
    public static ArrayList<User> searchFor(String searchString) {
        ArrayList<User> users = new ArrayList<>();
        
        try (Connection conn = Database.getConnection()){
            PreparedStatement statement = null;
            ResultSet rs = null;
            
            statement = conn.prepareStatement("select * from user where username like ?"
                    + " or profile_desc like ?");

            statement.setString(1, "%" + searchString + "%");
            statement.setString(2, "%" + searchString + "%");
            
            rs = statement.executeQuery();
            
            while (rs.next()) {
                
                int id_ = rs.getInt(1);
                String username_ = rs.getString(2);
                String email_ = rs.getString(3);
                String password_ = rs.getString(4);
                String profileDesc = rs.getString(5);
                String profilePhoto = rs.getString(6);
                String registeredAt = rs.getString(7);
                String lastLogin = rs.getString(8);
                String admin = rs.getString(9);
                
                User user = new User(id_, username_, email_, password_, profileDesc, profilePhoto, registeredAt, lastLogin, admin);
                
                
                users.add(user);
            }
            
            statement.close();
            conn.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Crypto.hashSHA256(password);
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }

    public String getProfilePhotoUrl() {
        return profilePhotoUrl;
    }

    public void setProfilePhotoUrl(String profilePhotoUrl) {
        this.profilePhotoUrl = profilePhotoUrl;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    
    public boolean isAdmin() {
        return this.admin.equals("1");
    }

    public void setAdmin(boolean admin) {
        this.admin = admin ? "1" : "0";
    }

    /**
     * Metoda pro smazání uživatele z databáze
     */
    public void delete() {
        ArrayList<String> params = new ArrayList<>();
        
        params.add(String.valueOf(id));
        
        Database
                .getInstance()
                .executeAction("delete from user where id_user=?"
                        , params);

    }
    
    
    
    
}
