insert into voter (name, password_hash, house_district_id,
                   senate_district_id, phone_number, address,
                   email, representing_district_id)
    values (?, ?, ?, ?, ?, ?, ?, ?);