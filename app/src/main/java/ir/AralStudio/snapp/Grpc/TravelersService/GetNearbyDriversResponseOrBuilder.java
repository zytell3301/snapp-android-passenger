// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: proto/TravelersService/TravelersService.proto

package ir.AralStudio.snapp.Grpc.TravelersService;

public interface GetNearbyDriversResponseOrBuilder extends
    // @@protoc_insertion_point(interface_extends:TravelersService.GetNearbyDriversResponse)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>repeated .TravelersService.driverLocation driver = 1;</code>
   */
  java.util.List<ir.AralStudio.snapp.Grpc.TravelersService.driverLocation> 
      getDriverList();
  /**
   * <code>repeated .TravelersService.driverLocation driver = 1;</code>
   */
  ir.AralStudio.snapp.Grpc.TravelersService.driverLocation getDriver(int index);
  /**
   * <code>repeated .TravelersService.driverLocation driver = 1;</code>
   */
  int getDriverCount();
  /**
   * <code>repeated .TravelersService.driverLocation driver = 1;</code>
   */
  java.util.List<? extends ir.AralStudio.snapp.Grpc.TravelersService.driverLocationOrBuilder> 
      getDriverOrBuilderList();
  /**
   * <code>repeated .TravelersService.driverLocation driver = 1;</code>
   */
  ir.AralStudio.snapp.Grpc.TravelersService.driverLocationOrBuilder getDriverOrBuilder(
      int index);
}
