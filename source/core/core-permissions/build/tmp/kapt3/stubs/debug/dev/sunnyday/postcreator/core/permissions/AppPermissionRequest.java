package dev.sunnyday.postcreator.core.permissions;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0001\bB\u001d\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0002\u0010\u0007\u0082\u0001\u0001\t\u00a8\u0006\n"}, d2 = {"Ldev/sunnyday/postcreator/core/permissions/AppPermissionRequest;", "Ldev/sunnyday/postcreator/core/permissions/PermissionRequest;", "requestCode", "", "permissions", "", "", "(I[Ljava/lang/String;)V", "Storage", "Ldev/sunnyday/postcreator/core/permissions/AppPermissionRequest$Storage;", "core-permissions_debug"})
public abstract class AppPermissionRequest extends dev.sunnyday.postcreator.core.permissions.PermissionRequest {
    
    private AppPermissionRequest(int requestCode, java.lang.String[] permissions) {
        super(0, null);
    }
    
    @kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Ldev/sunnyday/postcreator/core/permissions/AppPermissionRequest$Storage;", "Ldev/sunnyday/postcreator/core/permissions/AppPermissionRequest;", "()V", "core-permissions_debug"})
    public static final class Storage extends dev.sunnyday.postcreator.core.permissions.AppPermissionRequest {
        public static final dev.sunnyday.postcreator.core.permissions.AppPermissionRequest.Storage INSTANCE = null;
        
        private Storage() {
            super(0, null);
        }
    }
}