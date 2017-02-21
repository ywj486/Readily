package com.bc.ywjphone.readily.View;

/**
 * Created by Administrator on 2016/12/13 0013.
 */
public class SlideMenuItem{
        private int itemId;
        private String title;

    public SlideMenuItem(int itemId,String title){
        this.itemId=itemId;
        this.title=title;
    }
        public int getItemId() {
            return itemId;
        }

        public void setItemId(int itemId) {
            this.itemId = itemId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
}
