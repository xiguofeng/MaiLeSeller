
package com.o2o.maileseller.notify;


public class NotifyFactory {
	
	  public enum NotifyType {

	        NewOrder,

	        Version,

	        /**
	         * 活动
	         */
	        Activity;

	    }

    public static Notify create(NotifyType type) {
        Notify notify = null;
        if (NotifyType.NewOrder.equals(type)) {
            notify = new NewOrderNotify();
        } else if (NotifyType.Version.equals(type)) {
            //notify = new QueneNotify();  
        } else if (NotifyType.Activity.equals(type)) {
           // notify = new CommetNotify();
        }
        return notify;
    }

}
