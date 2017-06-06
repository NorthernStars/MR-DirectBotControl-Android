package android.widget;

public interface JoystickMovedListener {
        public void OnMoved(int aPan, int aTilt);
        public void OnReleased();
        public void OnReturnedToCenter();
}

