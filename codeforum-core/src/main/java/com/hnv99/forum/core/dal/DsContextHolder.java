package com.hnv99.forum.core.dal;

/**
 * Data Source Context Holder class, used to store which data source is currently selected
 */
public class DsContextHolder {
    /**
     * Use inherited thread context for asynchronous support
     * Use DsNode to support chain-like data source switching, such as using master data source at the outermost level
     * and using slave data source in an inner method. However, please note not to cross in transaction scenarios.
     */
    private static final ThreadLocal<DsNode> CONTEXT_HOLDER = new InheritableThreadLocal<>();

    private DsContextHolder() {
    }

    public static void set(String dbType) {
        DsNode current = CONTEXT_HOLDER.get();
        CONTEXT_HOLDER.set(new DsNode(current, dbType));
    }

    public static String get() {
        DsNode ds = CONTEXT_HOLDER.get();
        return ds == null ? null : ds.ds;
    }

    public static void set(DS ds) {
        set(ds.name().toUpperCase());
    }

    /**
     * Remove context
     */
    public static void reset() {
        DsNode ds = CONTEXT_HOLDER.get();
        if (ds == null) {
            return;
        }

        if (ds.pre != null) {
            // Exit current data source selection and revert to the previous data source configuration
            CONTEXT_HOLDER.set(ds.pre);
        } else {
            CONTEXT_HOLDER.remove();
        }
    }

    /**
     * Use the master data source type
     */
    public static void master() {
        set(MasterSlaveDsEnum.MASTER.name());
    }

    /**
     * Use the slave data source type
     */
    public static void slave() {
        set(MasterSlaveDsEnum.SLAVE.name());
    }

    public static class DsNode {
        DsNode pre;
        String ds;

        public DsNode(DsNode parent, String ds) {
            pre = parent;
            this.ds = ds;
        }
    }
}
