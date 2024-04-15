package com.hnv99.forum.core.dal;

/**
 * Enumeration for master-slave data sources
 */
public enum MasterSlaveDsEnum implements DS {
    /**
     * Master database type
     */
    MASTER,
    /**
     * Slave database type
     */
    SLAVE;
}
