/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.realtek.fullfactorymenu.api.listener;
public interface ICommandCallback extends android.os.IInterface
{
  /** Default implementation for ICommandCallback. */
  public static class Default implements com.realtek.fullfactorymenu.api.listener.ICommandCallback
  {
    @Override public void complete(int result, android.os.Bundle extras) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.realtek.fullfactorymenu.api.listener.ICommandCallback
  {
    private static final java.lang.String DESCRIPTOR = "com.realtek.fullfactorymenu.api.listener.ICommandCallback";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.realtek.fullfactorymenu.api.listener.ICommandCallback interface,
     * generating a proxy if needed.
     */
    public static com.realtek.fullfactorymenu.api.listener.ICommandCallback asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.realtek.fullfactorymenu.api.listener.ICommandCallback))) {
        return ((com.realtek.fullfactorymenu.api.listener.ICommandCallback)iin);
      }
      return new com.realtek.fullfactorymenu.api.listener.ICommandCallback.Stub.Proxy(obj);
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
        case TRANSACTION_complete:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          android.os.Bundle _arg1;
          if ((0!=data.readInt())) {
            _arg1 = android.os.Bundle.CREATOR.createFromParcel(data);
          }
          else {
            _arg1 = null;
          }
          this.complete(_arg0, _arg1);
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.realtek.fullfactorymenu.api.listener.ICommandCallback
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
      @Override public void complete(int result, android.os.Bundle extras) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(result);
          if ((extras!=null)) {
            _data.writeInt(1);
            extras.writeToParcel(_data, 0);
          }
          else {
            _data.writeInt(0);
          }
          boolean _status = mRemote.transact(Stub.TRANSACTION_complete, _data, null, android.os.IBinder.FLAG_ONEWAY);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().complete(result, extras);
            return;
          }
        }
        finally {
          _data.recycle();
        }
      }
      public static com.realtek.fullfactorymenu.api.listener.ICommandCallback sDefaultImpl;
    }
    static final int TRANSACTION_complete = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    public static boolean setDefaultImpl(com.realtek.fullfactorymenu.api.listener.ICommandCallback impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.realtek.fullfactorymenu.api.listener.ICommandCallback getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void complete(int result, android.os.Bundle extras) throws android.os.RemoteException;
}
