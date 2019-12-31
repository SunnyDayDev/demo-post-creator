package dev.sunnyday.postcreator.core.permissions;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0003\b\u0000\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0011\u001a\u00020\u0012J\u0012\u0010\u0013\u001a\u00020\u00122\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H\u0016J\b\u0010\u0016\u001a\u00020\u0012H\u0016J-\u0010\u0017\u001a\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u00192\u000e\u0010\u001a\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u001c0\u001b2\u0006\u0010\u001d\u001a\u00020\u001eH\u0016\u00a2\u0006\u0002\u0010\u001fJ\b\u0010 \u001a\u00020\u0012H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001c\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010\u00a8\u0006!"}, d2 = {"Ldev/sunnyday/postcreator/core/permissions/PermissionRequestFragment;", "Landroidx/fragment/app/Fragment;", "()V", "isDismissed", "", "request", "Ldev/sunnyday/postcreator/core/permissions/PermissionRequest;", "getRequest", "()Ldev/sunnyday/postcreator/core/permissions/PermissionRequest;", "setRequest", "(Ldev/sunnyday/postcreator/core/permissions/PermissionRequest;)V", "resultEmitter", "Lio/reactivex/CompletableEmitter;", "getResultEmitter", "()Lio/reactivex/CompletableEmitter;", "setResultEmitter", "(Lio/reactivex/CompletableEmitter;)V", "dismiss", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onRequestPermissionsResult", "requestCode", "", "permissions", "", "", "grantResults", "", "(I[Ljava/lang/String;[I)V", "onResume", "core-permissions_debug"})
public final class PermissionRequestFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.Nullable()
    private dev.sunnyday.postcreator.core.permissions.PermissionRequest request;
    @org.jetbrains.annotations.Nullable()
    private io.reactivex.CompletableEmitter resultEmitter;
    private boolean isDismissed;
    
    @org.jetbrains.annotations.Nullable()
    public final dev.sunnyday.postcreator.core.permissions.PermissionRequest getRequest() {
        return null;
    }
    
    public final void setRequest(@org.jetbrains.annotations.Nullable()
    dev.sunnyday.postcreator.core.permissions.PermissionRequest p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final io.reactivex.CompletableEmitter getResultEmitter() {
        return null;
    }
    
    public final void setResultEmitter(@org.jetbrains.annotations.Nullable()
    io.reactivex.CompletableEmitter p0) {
    }
    
    @java.lang.Override()
    public void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override()
    public void onResume() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @java.lang.Override()
    public void onRequestPermissionsResult(int requestCode, @org.jetbrains.annotations.NotNull()
    java.lang.String[] permissions, @org.jetbrains.annotations.NotNull()
    int[] grantResults) {
    }
    
    public final void dismiss() {
    }
    
    public PermissionRequestFragment() {
        super();
    }
}