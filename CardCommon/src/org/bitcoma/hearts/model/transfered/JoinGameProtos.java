// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: JoinGame.proto

package org.bitcoma.hearts.model.transfered;

public final class JoinGameProtos {
  private JoinGameProtos() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface JoinGameRequestOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
    
    // optional uint64 game_id = 1;
    boolean hasGameId();
    long getGameId();
  }
  public static final class JoinGameRequest extends
      com.google.protobuf.GeneratedMessage
      implements JoinGameRequestOrBuilder {
    // Use JoinGameRequest.newBuilder() to construct.
    private JoinGameRequest(Builder builder) {
      super(builder);
    }
    private JoinGameRequest(boolean noInit) {}
    
    private static final JoinGameRequest defaultInstance;
    public static JoinGameRequest getDefaultInstance() {
      return defaultInstance;
    }
    
    public JoinGameRequest getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.bitcoma.hearts.model.transfered.JoinGameProtos.internal_static_hearts_JoinGameRequest_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.bitcoma.hearts.model.transfered.JoinGameProtos.internal_static_hearts_JoinGameRequest_fieldAccessorTable;
    }
    
    private int bitField0_;
    // optional uint64 game_id = 1;
    public static final int GAME_ID_FIELD_NUMBER = 1;
    private long gameId_;
    public boolean hasGameId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public long getGameId() {
      return gameId_;
    }
    
    private void initFields() {
      gameId_ = 0L;
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeUInt64(1, gameId_);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt64Size(1, gameId_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    @java.lang.Override
    protected Object writeReplace() throws java.io.ObjectStreamException {
      return super.writeReplace();
    }
    
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequestOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.bitcoma.hearts.model.transfered.JoinGameProtos.internal_static_hearts_JoinGameRequest_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.bitcoma.hearts.model.transfered.JoinGameProtos.internal_static_hearts_JoinGameRequest_fieldAccessorTable;
      }
      
      // Construct using org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        gameId_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest.getDescriptor();
      }
      
      public org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest getDefaultInstanceForType() {
        return org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest.getDefaultInstance();
      }
      
      public org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest build() {
        org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest buildPartial() {
        org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest result = new org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.gameId_ = gameId_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest) {
          return mergeFrom((org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest other) {
        if (other == org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest.getDefaultInstance()) return this;
        if (other.hasGameId()) {
          setGameId(other.getGameId());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public final boolean isInitialized() {
        return true;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              onChanged();
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                onChanged();
                return this;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              gameId_ = input.readUInt64();
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // optional uint64 game_id = 1;
      private long gameId_ ;
      public boolean hasGameId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public long getGameId() {
        return gameId_;
      }
      public Builder setGameId(long value) {
        bitField0_ |= 0x00000001;
        gameId_ = value;
        onChanged();
        return this;
      }
      public Builder clearGameId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        gameId_ = 0L;
        onChanged();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:hearts.JoinGameRequest)
    }
    
    static {
      defaultInstance = new JoinGameRequest(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:hearts.JoinGameRequest)
  }
  
  public interface JoinGameResponseOrBuilder
      extends com.google.protobuf.MessageOrBuilder {
    
    // optional .hearts.GameInfo game_info = 1;
    boolean hasGameInfo();
    org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo getGameInfo();
    org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfoOrBuilder getGameInfoOrBuilder();
  }
  public static final class JoinGameResponse extends
      com.google.protobuf.GeneratedMessage
      implements JoinGameResponseOrBuilder {
    // Use JoinGameResponse.newBuilder() to construct.
    private JoinGameResponse(Builder builder) {
      super(builder);
    }
    private JoinGameResponse(boolean noInit) {}
    
    private static final JoinGameResponse defaultInstance;
    public static JoinGameResponse getDefaultInstance() {
      return defaultInstance;
    }
    
    public JoinGameResponse getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.bitcoma.hearts.model.transfered.JoinGameProtos.internal_static_hearts_JoinGameResponse_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.bitcoma.hearts.model.transfered.JoinGameProtos.internal_static_hearts_JoinGameResponse_fieldAccessorTable;
    }
    
    private int bitField0_;
    // optional .hearts.GameInfo game_info = 1;
    public static final int GAME_INFO_FIELD_NUMBER = 1;
    private org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo gameInfo_;
    public boolean hasGameInfo() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    public org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo getGameInfo() {
      return gameInfo_;
    }
    public org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfoOrBuilder getGameInfoOrBuilder() {
      return gameInfo_;
    }
    
    private void initFields() {
      gameInfo_ = org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.getDefaultInstance();
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;
      
      memoizedIsInitialized = 1;
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeMessage(1, gameInfo_);
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, gameInfo_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    @java.lang.Override
    protected Object writeReplace() throws java.io.ObjectStreamException {
      return super.writeReplace();
    }
    
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponseOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.bitcoma.hearts.model.transfered.JoinGameProtos.internal_static_hearts_JoinGameResponse_descriptor;
      }
      
      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.bitcoma.hearts.model.transfered.JoinGameProtos.internal_static_hearts_JoinGameResponse_fieldAccessorTable;
      }
      
      // Construct using org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }
      
      private Builder(BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
          getGameInfoFieldBuilder();
        }
      }
      private static Builder create() {
        return new Builder();
      }
      
      public Builder clear() {
        super.clear();
        if (gameInfoBuilder_ == null) {
          gameInfo_ = org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.getDefaultInstance();
        } else {
          gameInfoBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse.getDescriptor();
      }
      
      public org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse getDefaultInstanceForType() {
        return org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse.getDefaultInstance();
      }
      
      public org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse build() {
        org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }
      
      private org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return result;
      }
      
      public org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse buildPartial() {
        org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse result = new org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        if (gameInfoBuilder_ == null) {
          result.gameInfo_ = gameInfo_;
        } else {
          result.gameInfo_ = gameInfoBuilder_.build();
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse) {
          return mergeFrom((org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse other) {
        if (other == org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse.getDefaultInstance()) return this;
        if (other.hasGameInfo()) {
          mergeGameInfo(other.getGameInfo());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public final boolean isInitialized() {
        return true;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              onChanged();
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                onChanged();
                return this;
              }
              break;
            }
            case 10: {
              org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.Builder subBuilder = org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.newBuilder();
              if (hasGameInfo()) {
                subBuilder.mergeFrom(getGameInfo());
              }
              input.readMessage(subBuilder, extensionRegistry);
              setGameInfo(subBuilder.buildPartial());
              break;
            }
          }
        }
      }
      
      private int bitField0_;
      
      // optional .hearts.GameInfo game_info = 1;
      private org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo gameInfo_ = org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.getDefaultInstance();
      private com.google.protobuf.SingleFieldBuilder<
          org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo, org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.Builder, org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfoOrBuilder> gameInfoBuilder_;
      public boolean hasGameInfo() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      public org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo getGameInfo() {
        if (gameInfoBuilder_ == null) {
          return gameInfo_;
        } else {
          return gameInfoBuilder_.getMessage();
        }
      }
      public Builder setGameInfo(org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo value) {
        if (gameInfoBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          gameInfo_ = value;
          onChanged();
        } else {
          gameInfoBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      public Builder setGameInfo(
          org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.Builder builderForValue) {
        if (gameInfoBuilder_ == null) {
          gameInfo_ = builderForValue.build();
          onChanged();
        } else {
          gameInfoBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      public Builder mergeGameInfo(org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo value) {
        if (gameInfoBuilder_ == null) {
          if (((bitField0_ & 0x00000001) == 0x00000001) &&
              gameInfo_ != org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.getDefaultInstance()) {
            gameInfo_ =
              org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.newBuilder(gameInfo_).mergeFrom(value).buildPartial();
          } else {
            gameInfo_ = value;
          }
          onChanged();
        } else {
          gameInfoBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      public Builder clearGameInfo() {
        if (gameInfoBuilder_ == null) {
          gameInfo_ = org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.getDefaultInstance();
          onChanged();
        } else {
          gameInfoBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      public org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.Builder getGameInfoBuilder() {
        bitField0_ |= 0x00000001;
        onChanged();
        return getGameInfoFieldBuilder().getBuilder();
      }
      public org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfoOrBuilder getGameInfoOrBuilder() {
        if (gameInfoBuilder_ != null) {
          return gameInfoBuilder_.getMessageOrBuilder();
        } else {
          return gameInfo_;
        }
      }
      private com.google.protobuf.SingleFieldBuilder<
          org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo, org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.Builder, org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfoOrBuilder> 
          getGameInfoFieldBuilder() {
        if (gameInfoBuilder_ == null) {
          gameInfoBuilder_ = new com.google.protobuf.SingleFieldBuilder<
              org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo, org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfo.Builder, org.bitcoma.hearts.model.transfered.GameStructProtos.GameInfoOrBuilder>(
                  gameInfo_,
                  getParentForChildren(),
                  isClean());
          gameInfo_ = null;
        }
        return gameInfoBuilder_;
      }
      
      // @@protoc_insertion_point(builder_scope:hearts.JoinGameResponse)
    }
    
    static {
      defaultInstance = new JoinGameResponse(true);
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:hearts.JoinGameResponse)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_hearts_JoinGameRequest_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_hearts_JoinGameRequest_fieldAccessorTable;
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_hearts_JoinGameResponse_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_hearts_JoinGameResponse_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016JoinGame.proto\022\006hearts\032\020GameStruct.pro" +
      "to\"\"\n\017JoinGameRequest\022\017\n\007game_id\030\001 \001(\004\"7" +
      "\n\020JoinGameResponse\022#\n\tgame_info\030\001 \001(\0132\020." +
      "hearts.GameInfoB7\n#org.bitcoma.hearts.mo" +
      "del.transferedB\016JoinGameProtosH\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_hearts_JoinGameRequest_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_hearts_JoinGameRequest_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_hearts_JoinGameRequest_descriptor,
              new java.lang.String[] { "GameId", },
              org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest.class,
              org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameRequest.Builder.class);
          internal_static_hearts_JoinGameResponse_descriptor =
            getDescriptor().getMessageTypes().get(1);
          internal_static_hearts_JoinGameResponse_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_hearts_JoinGameResponse_descriptor,
              new java.lang.String[] { "GameInfo", },
              org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse.class,
              org.bitcoma.hearts.model.transfered.JoinGameProtos.JoinGameResponse.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          org.bitcoma.hearts.model.transfered.GameStructProtos.getDescriptor(),
        }, assigner);
  }
  
  // @@protoc_insertion_point(outer_class_scope)
}
