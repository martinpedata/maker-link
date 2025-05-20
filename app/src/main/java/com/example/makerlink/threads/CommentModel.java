package com.example.makerlink.threads;

public class CommentModel {
    private String user;
    private String commentContent;
    private int likes;
    private int id;
    private boolean upvoted;
    public CommentModel(int id, String user, String comment, int likes) {
        this.id = id;
        this.user = user;
        this.commentContent = comment;
        this.likes = likes;
        upvoted = false;
    }

    public int getID() {
        return id;
    }

    public void setUpVoted(){
        upvoted = true;
    }
    public void setDownVoted() {
        upvoted = false;
    }

    public boolean isUpvoted() {
        return upvoted;
    }
    public String getNameuser() {
        return user;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void removeLike() {
        likes--;
    }
    public void addLike() {
        likes++;
    }

    public int getLikes() {
        return likes;
    }
}
