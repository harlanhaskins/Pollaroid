package com.bipoller.models;

import com.bipoller.database.SQLUtils;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class Poll {
    private long id;
    private Voter submitter;
    private District district;
    private String title;
}
