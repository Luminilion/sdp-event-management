package ch.epfl.sweng.eventmanager.ui.ticketing;

import dagger.Module;
import dagger.Provides;

/**
 * @author Louis Vialar
 */
@Module
public class BarcodeViewManagerModule {
    @Provides
    BarcodeViewWrapper barcodeViewManager() {
        return new BarcodeViewWrapperImpl();
    }
}

