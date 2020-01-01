package dev.sunnyday.postcreator.core.permissions;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0015\n\u0002\b\u0005\b\u0000\u0018\u0000 \u00182\u00020\u0001:\u0002\u0018\u0019B\u0007\b\u0016\u00a2\u0006\u0002\u0010\u0002B\u0011\b\u0002\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004\u00a2\u0006\u0002\u0010\u0005J\u0006\u0010\b\u001a\u00020\tJ\u0012\u0010\n\u001a\u00020\t2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0016J\b\u0010\r\u001a\u00020\tH\u0016J-\u0010\u000e\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u00102\u000e\u0010\u0011\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00130\u00122\u0006\u0010\u0014\u001a\u00020\u0015H\u0016\u00a2\u0006\u0002\u0010\u0016J\b\u0010\u0017\u001a\u00020\tH\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Ldev/sunnyday/postcreator/core/permissions/PermissionRequestFragment;", "Landroidx/fragment/app/Fragment;", "()V", "executingRequest", "Ldev/sunnyday/postcreator/core/permissions/PermissionRequestFragment$ExecutingPermissionRequest;", "(Ldev/sunnyday/postcreator/core/permissions/PermissionRequestFragment$ExecutingPermissionRequest;)V", "isDismissed", "", "dismiss", "", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onRequestPermissionsResult", "requestCode", "", "permissions", "", "", "grantResults", "", "(I[Ljava/lang/String;[I)V", "onResume", "Companion", "ExecutingPermissionRequest", "core-permissions_debug"})
public final class PermissionRequestFragment extends androidx.fragment.app.Fragment {
    private boolean isDismissed;
    private final dev.sunnyday.postcreator.core.permissions.PermissionRequestFragment.ExecutingPermissionRequest executingRequest = null;
    public static final dev.sunnyday.postcreator.core.permissions.PermissionRequestFragment.Companion Companion = null;
    
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
    
    private PermissionRequestFragment(dev.sunnyday.postcreator.core.permissions.PermissionRequestFragment.ExecutingPermissionRequest executingRequest) {
        super();
    }
    
    public PermissionRequestFragment() {
        super();
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\b\u0018\u00002\u00020\u0001B3\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0016\u0010\u0007\u001a\u0012\u0012\b\u0012\u00060\tj\u0002`\n\u0012\u0004\u0012\u00020\u00060\b\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u0019\u0010\u0014\u001a\u0012\u0012\b\u0012\u00060\tj\u0002`\n\u0012\u0004\u0012\u00020\u00060\bH\u00c6\u0003J=\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\u0018\b\u0002\u0010\u0007\u001a\u0012\u0012\b\u0012\u00060\tj\u0002`\n\u0012\u0004\u0012\u00020\u00060\bH\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001R!\u0010\u0007\u001a\u0012\u0012\b\u0012\u00060\tj\u0002`\n\u0012\u0004\u0012\u00020\u00060\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u001d"}, d2 = {"Ldev/sunnyday/postcreator/core/permissions/PermissionRequestFragment$ExecutingPermissionRequest;", "", "request", "Ldev/sunnyday/postcreator/core/permissions/PermissionRequest;", "onSuccess", "Lkotlin/Function0;", "", "onError", "Lkotlin/Function1;", "Ljava/lang/Error;", "Lkotlin/Error;", "(Ldev/sunnyday/postcreator/core/permissions/PermissionRequest;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;)V", "getOnError", "()Lkotlin/jvm/functions/Function1;", "getOnSuccess", "()Lkotlin/jvm/functions/Function0;", "getRequest", "()Ldev/sunnyday/postcreator/core/permissions/PermissionRequest;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "", "core-permissions_debug"})
    static final class ExecutingPermissionRequest {
        @org.jetbrains.annotations.NotNull()
        private final dev.sunnyday.postcreator.core.permissions.PermissionRequest request = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<java.lang.Error, kotlin.Unit> onError = null;
        
        @org.jetbrains.annotations.NotNull()
        public final dev.sunnyday.postcreator.core.permissions.PermissionRequest getRequest() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlin.jvm.functions.Function0<kotlin.Unit> getOnSuccess() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlin.jvm.functions.Function1<java.lang.Error, kotlin.Unit> getOnError() {
            return null;
        }
        
        public ExecutingPermissionRequest(@org.jetbrains.annotations.NotNull()
        dev.sunnyday.postcreator.core.permissions.PermissionRequest request, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super java.lang.Error, kotlin.Unit> onError) {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final dev.sunnyday.postcreator.core.permissions.PermissionRequest component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlin.jvm.functions.Function0<kotlin.Unit> component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final kotlin.jvm.functions.Function1<java.lang.Error, kotlin.Unit> component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final dev.sunnyday.postcreator.core.permissions.PermissionRequestFragment.ExecutingPermissionRequest copy(@org.jetbrains.annotations.NotNull()
        dev.sunnyday.postcreator.core.permissions.PermissionRequest request, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super java.lang.Error, kotlin.Unit> onError) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        @java.lang.Override()
        public java.lang.String toString() {
            return null;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object p0) {
            return false;
        }
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J4\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0016\u0010\n\u001a\u0012\u0012\b\u0012\u00060\fj\u0002`\r\u0012\u0004\u0012\u00020\t0\u000b\u00a8\u0006\u000e"}, d2 = {"Ldev/sunnyday/postcreator/core/permissions/PermissionRequestFragment$Companion;", "", "()V", "create", "Ldev/sunnyday/postcreator/core/permissions/PermissionRequestFragment;", "request", "Ldev/sunnyday/postcreator/core/permissions/PermissionRequest;", "onSuccess", "Lkotlin/Function0;", "", "onError", "Lkotlin/Function1;", "Ljava/lang/Error;", "Lkotlin/Error;", "core-permissions_debug"})
    public static final class Companion {
        
        @org.jetbrains.annotations.NotNull()
        public final dev.sunnyday.postcreator.core.permissions.PermissionRequestFragment create(@org.jetbrains.annotations.NotNull()
        dev.sunnyday.postcreator.core.permissions.PermissionRequest request, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function0<kotlin.Unit> onSuccess, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super java.lang.Error, kotlin.Unit> onError) {
            return null;
        }
        
        private Companion() {
            super();
        }
    }
}