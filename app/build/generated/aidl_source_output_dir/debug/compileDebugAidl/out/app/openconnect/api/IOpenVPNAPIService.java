/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: F:\\Room\\Apps\\Projects\\SpeedVPN\\SpeedVpn\\app\\src\\main\\aidl\\app\\openconnect\\api\\IOpenVPNAPIService.aidl
 */
package app.openconnect.api;
public interface IOpenVPNAPIService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements app.openconnect.api.IOpenVPNAPIService
{
private static final java.lang.String DESCRIPTOR = "app.openconnect.api.IOpenVPNAPIService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an app.openconnect.api.IOpenVPNAPIService interface,
 * generating a proxy if needed.
 */
public static app.openconnect.api.IOpenVPNAPIService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof app.openconnect.api.IOpenVPNAPIService))) {
return ((app.openconnect.api.IOpenVPNAPIService)iin);
}
return new app.openconnect.api.IOpenVPNAPIService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
java.lang.String descriptor = DESCRIPTOR;
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(descriptor);
return true;
}
case TRANSACTION_getProfiles:
{
data.enforceInterface(descriptor);
java.util.List<app.openconnect.api.APIVpnProfile> _result = this.getProfiles();
reply.writeNoException();
reply.writeTypedList(_result);
return true;
}
case TRANSACTION_startProfile:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
this.startProfile(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_addVPNProfile:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
boolean _result = this.addVPNProfile(_arg0, _arg1);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_startVPN:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
this.startVPN(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_prepare:
{
data.enforceInterface(descriptor);
java.lang.String _arg0;
_arg0 = data.readString();
android.content.Intent _result = this.prepare(_arg0);
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_prepareVPNService:
{
data.enforceInterface(descriptor);
android.content.Intent _result = this.prepareVPNService();
reply.writeNoException();
if ((_result!=null)) {
reply.writeInt(1);
_result.writeToParcel(reply, android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
}
else {
reply.writeInt(0);
}
return true;
}
case TRANSACTION_disconnect:
{
data.enforceInterface(descriptor);
this.disconnect();
reply.writeNoException();
return true;
}
case TRANSACTION_registerStatusCallback:
{
data.enforceInterface(descriptor);
app.openconnect.api.IOpenVPNStatusCallback _arg0;
_arg0 = app.openconnect.api.IOpenVPNStatusCallback.Stub.asInterface(data.readStrongBinder());
this.registerStatusCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterStatusCallback:
{
data.enforceInterface(descriptor);
app.openconnect.api.IOpenVPNStatusCallback _arg0;
_arg0 = app.openconnect.api.IOpenVPNStatusCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterStatusCallback(_arg0);
reply.writeNoException();
return true;
}
default:
{
return super.onTransact(code, data, reply, flags);
}
}
}
private static class Proxy implements app.openconnect.api.IOpenVPNAPIService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public java.util.List<app.openconnect.api.APIVpnProfile> getProfiles() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.util.List<app.openconnect.api.APIVpnProfile> _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getProfiles, _data, _reply, 0);
_reply.readException();
_result = _reply.createTypedArrayList(app.openconnect.api.APIVpnProfile.CREATOR);
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
@Override public void startProfile(java.lang.String profileUUID) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(profileUUID);
mRemote.transact(Stub.TRANSACTION_startProfile, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** Use a profile with all certificates etc. embedded */
@Override public boolean addVPNProfile(java.lang.String name, java.lang.String config) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(name);
_data.writeString(config);
mRemote.transact(Stub.TRANSACTION_addVPNProfile, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** start a profile using an config */
@Override public void startVPN(java.lang.String inlineconfig) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(inlineconfig);
mRemote.transact(Stub.TRANSACTION_startVPN, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/** This permission framework is used  to avoid confused deputy style attack to the VPN
	 * calling this will give null if the app is allowed to use the external API and an Intent
	 * that can be launched to request permissions otherwise */
@Override public android.content.Intent prepare(java.lang.String packagename) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.content.Intent _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(packagename);
mRemote.transact(Stub.TRANSACTION_prepare, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.content.Intent.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/** Used to trigger to the Android VPN permission dialog (VPNService.prepare()) in advance,
	 * if this return null OpenVPN for ANdroid already has the permissions otherwise you can start the returned Intent
	 * to let OpenVPN for Android request the permission */
@Override public android.content.Intent prepareVPNService() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
android.content.Intent _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_prepareVPNService, _data, _reply, 0);
_reply.readException();
if ((0!=_reply.readInt())) {
_result = android.content.Intent.CREATOR.createFromParcel(_reply);
}
else {
_result = null;
}
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
/* Disconnect the VPN */
@Override public void disconnect() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_disconnect, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
      * Registers to receive OpenVPN Status Updates
      */
@Override public void registerStatusCallback(app.openconnect.api.IOpenVPNStatusCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerStatusCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
/**
     * Remove a previously registered callback interface.
     */
@Override public void unregisterStatusCallback(app.openconnect.api.IOpenVPNStatusCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterStatusCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_getProfiles = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_startProfile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_addVPNProfile = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_startVPN = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_prepare = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
static final int TRANSACTION_prepareVPNService = (android.os.IBinder.FIRST_CALL_TRANSACTION + 5);
static final int TRANSACTION_disconnect = (android.os.IBinder.FIRST_CALL_TRANSACTION + 6);
static final int TRANSACTION_registerStatusCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 7);
static final int TRANSACTION_unregisterStatusCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 8);
}
public java.util.List<app.openconnect.api.APIVpnProfile> getProfiles() throws android.os.RemoteException;
public void startProfile(java.lang.String profileUUID) throws android.os.RemoteException;
/** Use a profile with all certificates etc. embedded */
public boolean addVPNProfile(java.lang.String name, java.lang.String config) throws android.os.RemoteException;
/** start a profile using an config */
public void startVPN(java.lang.String inlineconfig) throws android.os.RemoteException;
/** This permission framework is used  to avoid confused deputy style attack to the VPN
	 * calling this will give null if the app is allowed to use the external API and an Intent
	 * that can be launched to request permissions otherwise */
public android.content.Intent prepare(java.lang.String packagename) throws android.os.RemoteException;
/** Used to trigger to the Android VPN permission dialog (VPNService.prepare()) in advance,
	 * if this return null OpenVPN for ANdroid already has the permissions otherwise you can start the returned Intent
	 * to let OpenVPN for Android request the permission */
public android.content.Intent prepareVPNService() throws android.os.RemoteException;
/* Disconnect the VPN */
public void disconnect() throws android.os.RemoteException;
/**
      * Registers to receive OpenVPN Status Updates
      */
public void registerStatusCallback(app.openconnect.api.IOpenVPNStatusCallback cb) throws android.os.RemoteException;
/**
     * Remove a previously registered callback interface.
     */
public void unregisterStatusCallback(app.openconnect.api.IOpenVPNStatusCallback cb) throws android.os.RemoteException;
}
