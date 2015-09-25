// Code generated by Wire protocol buffer compiler, do not edit.
// Source file: ./hackernews.proto
package hackernews;

import com.squareup.wire.Message;
import com.squareup.wire.ProtoField;
import java.util.Collections;
import java.util.List;

import static com.squareup.wire.Message.Datatype.BOOL;
import static com.squareup.wire.Message.Datatype.INT32;
import static com.squareup.wire.Message.Datatype.INT64;
import static com.squareup.wire.Message.Datatype.STRING;
import static com.squareup.wire.Message.Label.PACKED;
import static com.squareup.wire.Message.Label.REPEATED;
import static com.squareup.wire.Message.Label.REQUIRED;

public final class PbComment extends Message {
  private static final long serialVersionUID = 0L;

  public static final Long DEFAULT_ID = 0L;
  public static final String DEFAULT_AUTHOR = "";
  public static final Long DEFAULT_PARENT_ID = 0L;
  public static final Long DEFAULT_TIME = 0L;
  public static final String DEFAULT_TEXT = "";
  public static final List<Long> DEFAULT_COMMENT_IDS = Collections.emptyList();
  public static final Integer DEFAULT_COMMENT_COUNT = 0;
  public static final List<PbComment> DEFAULT_COMMENTS = Collections.emptyList();
  public static final Boolean DEFAULT_DELETED = false;
  public static final Boolean DEFAULT_DEAD = false;

  @ProtoField(tag = 1, type = INT64, label = REQUIRED)
  public final Long id;

  @ProtoField(tag = 2, type = STRING)
  public final String author;

  @ProtoField(tag = 3, type = INT64)
  public final Long parent_id;

  @ProtoField(tag = 4, type = INT64, label = REQUIRED)
  public final Long time;

  @ProtoField(tag = 5, type = STRING)
  public final String text;

  @ProtoField(tag = 6, type = INT64, label = PACKED)
  public final List<Long> comment_ids;

  @ProtoField(tag = 7, type = INT32)
  public final Integer comment_count;

  @ProtoField(tag = 8, label = REPEATED, messageType = PbComment.class)
  public final List<PbComment> comments;

  @ProtoField(tag = 9, type = BOOL)
  public final Boolean deleted;

  @ProtoField(tag = 10, type = BOOL)
  public final Boolean dead;

  public PbComment(Long id, String author, Long parent_id, Long time, String text, List<Long> comment_ids, Integer comment_count, List<PbComment> comments, Boolean deleted, Boolean dead) {
    this.id = id;
    this.author = author;
    this.parent_id = parent_id;
    this.time = time;
    this.text = text;
    this.comment_ids = immutableCopyOf(comment_ids);
    this.comment_count = comment_count;
    this.comments = immutableCopyOf(comments);
    this.deleted = deleted;
    this.dead = dead;
  }

  private PbComment(Builder builder) {
    this(builder.id, builder.author, builder.parent_id, builder.time, builder.text, builder.comment_ids, builder.comment_count, builder.comments, builder.deleted, builder.dead);
    setBuilder(builder);
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) return true;
    if (!(other instanceof PbComment)) return false;
    PbComment o = (PbComment) other;
    return equals(id, o.id)
        && equals(author, o.author)
        && equals(parent_id, o.parent_id)
        && equals(time, o.time)
        && equals(text, o.text)
        && equals(comment_ids, o.comment_ids)
        && equals(comment_count, o.comment_count)
        && equals(comments, o.comments)
        && equals(deleted, o.deleted)
        && equals(dead, o.dead);
  }

  @Override
  public int hashCode() {
    int result = hashCode;
    if (result == 0) {
      result = id != null ? id.hashCode() : 0;
      result = result * 37 + (author != null ? author.hashCode() : 0);
      result = result * 37 + (parent_id != null ? parent_id.hashCode() : 0);
      result = result * 37 + (time != null ? time.hashCode() : 0);
      result = result * 37 + (text != null ? text.hashCode() : 0);
      result = result * 37 + (comment_ids != null ? comment_ids.hashCode() : 1);
      result = result * 37 + (comment_count != null ? comment_count.hashCode() : 0);
      result = result * 37 + (comments != null ? comments.hashCode() : 1);
      result = result * 37 + (deleted != null ? deleted.hashCode() : 0);
      result = result * 37 + (dead != null ? dead.hashCode() : 0);
      hashCode = result;
    }
    return result;
  }

  public static final class Builder extends Message.Builder<PbComment> {

    public Long id;
    public String author;
    public Long parent_id;
    public Long time;
    public String text;
    public List<Long> comment_ids;
    public Integer comment_count;
    public List<PbComment> comments;
    public Boolean deleted;
    public Boolean dead;

    public Builder() {
    }

    public Builder(PbComment message) {
      super(message);
      if (message == null) return;
      this.id = message.id;
      this.author = message.author;
      this.parent_id = message.parent_id;
      this.time = message.time;
      this.text = message.text;
      this.comment_ids = copyOf(message.comment_ids);
      this.comment_count = message.comment_count;
      this.comments = copyOf(message.comments);
      this.deleted = message.deleted;
      this.dead = message.dead;
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder author(String author) {
      this.author = author;
      return this;
    }

    public Builder parent_id(Long parent_id) {
      this.parent_id = parent_id;
      return this;
    }

    public Builder time(Long time) {
      this.time = time;
      return this;
    }

    public Builder text(String text) {
      this.text = text;
      return this;
    }

    public Builder comment_ids(List<Long> comment_ids) {
      this.comment_ids = checkForNulls(comment_ids);
      return this;
    }

    public Builder comment_count(Integer comment_count) {
      this.comment_count = comment_count;
      return this;
    }

    public Builder comments(List<PbComment> comments) {
      this.comments = checkForNulls(comments);
      return this;
    }

    public Builder deleted(Boolean deleted) {
      this.deleted = deleted;
      return this;
    }

    public Builder dead(Boolean dead) {
      this.dead = dead;
      return this;
    }

    @Override
    public PbComment build() {
      checkRequiredFields();
      return new PbComment(this);
    }
  }
}
