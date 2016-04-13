package com.eddie.garj;

import java.util.Date;

/**
 * Created by eddie on 20/03/2016.
 */
public class GarageStatus {


        public final static String GARAGE_CLOSED = "Garage is Closed";
        public final static String GARAGE_OPEN = "Garage is Open";
        public final static String CLOSE_GARAGE = "Close Garage";
        public final static String OPEN_GARAGE = "Open Garage";
        public final static String GARAGE_STUCK = "Garage is Stuck";
        public final static String STATUS_SINCE = "Status Since: ";

        private String status = "Status Unknown";
        private Date lastChanged = new Date();

        public String getStatus() {
            return this.status;
        }
        public Date getLastChanged() {return this.lastChanged;  }

        public void setStatus(String status) {
                this.status = status;
        }
}
