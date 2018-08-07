package com.example.demo.utils;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Codebeautify {
 @JsonProperty("columns")
 ArrayList < Object > columns = new ArrayList < Object > ();
 @JsonProperty("data")
 ArrayList < Object > data = new ArrayList < Object > ();
 @JsonProperty("query_string")
 private String query_string;
 @JsonProperty("execution_time")
 private float execution_time;


 
 public String getQuery_string() {
  return query_string;
 }

 public float getExecution_time() {
  return execution_time;
 }

 // Setter Methods 

 public void setQuery_string(String query_string) {
  this.query_string = query_string;
 }

 public void setExecution_time(float execution_time) {
  this.execution_time = execution_time;
 }
}