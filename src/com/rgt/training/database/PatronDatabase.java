package com.rgt.training.database;

import com.rgt.training.models.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.rgt.training.utils.DBUtils;

public class PatronDatabase {
	
	// Check if a patron exists in the database
	
	public boolean isPatronExists(int patronId) {
		String sql = "SELECT COUNT(*) FROM patrons WHERE patronId = ?";
		try (Connection conn = DBUtils.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, patronId);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt(1) > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	// Add a patron to the database
	
	public void addPatron(Patrons patron) {
		String query = "INSERT INTO patrons (patronId, name) VALUES (?, ?)";
		try (Connection conn = DBUtils.getConnection(); PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, patron.getPatronId());
			ps.setString(2, patron.getName());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Get patron by ID
	
	public Patrons getPatronById(int patronId) {
		String query = "SELECT * FROM patrons WHERE patronId = ?";
		try (Connection connection = DBUtils.getConnection();
				PreparedStatement ps = connection.prepareStatement(query)) {
			ps.setInt(1, patronId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return new Patrons(rs.getInt("patronId"), rs.getString("name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Get all patrons from the database
	
	public List<Patrons> getAllPatrons() {
		List<Patrons> patrons = new ArrayList<>();
		String query = "SELECT * FROM patrons";
		try (Connection connection = DBUtils.getConnection();
				PreparedStatement ps = connection.prepareStatement(query);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				Patrons patron = new Patrons(rs.getInt("patronId"), rs.getString("name"));
				patrons.add(patron);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return patrons;
	}

}
