package main.api.response;

import lombok.Data;

import java.util.Date;

@Data
public class StatResponse {

    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private long firstPublication;
}
