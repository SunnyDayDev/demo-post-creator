package dev.sunnyday.postcreator.core.permissions.di;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\ba\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'\u00a8\u0006\u0006"}, d2 = {"Ldev/sunnyday/postcreator/core/permissions/di/InternalPermissionsModule;", "", "bindPermissionsInteractor", "Ldev/sunnyday/postcreator/core/permissions/PermissionRequestInteractor;", "impl", "Ldev/sunnyday/postcreator/core/permissions/PermissionRequestInteractorImpl;", "core-permissions_debug"})
@dagger.Module()
public abstract interface InternalPermissionsModule {
    
    @org.jetbrains.annotations.NotNull()
    @dagger.Binds()
    public abstract dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractor bindPermissionsInteractor(@org.jetbrains.annotations.NotNull()
    dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractorImpl impl);
}