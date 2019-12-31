package dev.sunnyday.postcreator.core.permissions;

import java.lang.System;

@kotlin.Metadata(mv = {1, 1, 16}, bv = {1, 0, 3}, k = 1, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0001\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J \u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\t\u001a\u00020\nH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000f"}, d2 = {"Ldev/sunnyday/postcreator/core/permissions/PermissionRequestInteractorImpl;", "Ldev/sunnyday/postcreator/core/permissions/PermissionRequestInteractor;", "activityObserver", "Ldev/sunnyday/postcreator/core/ui/ActivityObserver;", "(Ldev/sunnyday/postcreator/core/ui/ActivityObserver;)V", "requestPermission", "", "activity", "Landroidx/fragment/app/FragmentActivity;", "request", "Ldev/sunnyday/postcreator/core/permissions/PermissionRequest;", "emitter", "Lio/reactivex/CompletableEmitter;", "requirePermission", "Lio/reactivex/Completable;", "core-permissions_debug"})
@javax.inject.Singleton()
public final class PermissionRequestInteractorImpl implements dev.sunnyday.postcreator.core.permissions.PermissionRequestInteractor {
    private final dev.sunnyday.postcreator.core.ui.ActivityObserver activityObserver = null;
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public io.reactivex.Completable requirePermission(@org.jetbrains.annotations.NotNull()
    dev.sunnyday.postcreator.core.permissions.PermissionRequest request) {
        return null;
    }
    
    private final void requestPermission(androidx.fragment.app.FragmentActivity activity, dev.sunnyday.postcreator.core.permissions.PermissionRequest request, io.reactivex.CompletableEmitter emitter) {
    }
    
    @javax.inject.Inject()
    public PermissionRequestInteractorImpl(@org.jetbrains.annotations.NotNull()
    dev.sunnyday.postcreator.core.ui.ActivityObserver activityObserver) {
        super();
    }
}