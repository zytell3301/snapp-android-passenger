// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: TravelersService.proto

package ir.AralStudio.snapp.Grpc.GrpcServices;

public interface userOrBuilder extends
    // @@protoc_insertion_point(interface_extends:TravelersService.user)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>string id = 1;</code>
   * @return The id.
   */
  java.lang.String getId();
  /**
   * <code>string id = 1;</code>
   * @return The bytes for id.
   */
  com.google.protobuf.ByteString
      getIdBytes();

  /**
   * <code>.TravelersService.driver driver_details = 2;</code>
   * @return Whether the driverDetails field is set.
   */
  boolean hasDriverDetails();
  /**
   * <code>.TravelersService.driver driver_details = 2;</code>
   * @return The driverDetails.
   */
  ir.AralStudio.snapp.Grpc.GrpcServices.driver getDriverDetails();
  /**
   * <code>.TravelersService.driver driver_details = 2;</code>
   */
  ir.AralStudio.snapp.Grpc.GrpcServices.driverOrBuilder getDriverDetailsOrBuilder();
}