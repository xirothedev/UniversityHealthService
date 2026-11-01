package com.uhs.ui;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import javax.swing.SwingWorker;

/** Runs a database call off the Swing event thread and hands the outcome back on it. */
final class Background {
  private Background() {}

  static <T> void run(Callable<T> work, Consumer<T> onDone, Consumer<Exception> onError) {
    new SwingWorker<T, Void>() {
      @Override
      protected T doInBackground() throws Exception {
        return work.call();
      }

      @Override
      protected void done() {
        try {
          onDone.accept(get());
        } catch (java.util.concurrent.ExecutionException e) {
          Throwable cause = e.getCause();
          onError.accept(cause instanceof Exception ex ? ex : new Exception(cause));
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      }
    }.execute();
  }
}
