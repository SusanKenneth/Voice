package de.ph1b.audiobook.playback

import de.ph1b.audiobook.injection.PerService
import de.ph1b.audiobook.injection.PrefKeys
import de.ph1b.audiobook.persistence.BookRepository
import de.ph1b.audiobook.persistence.pref.Pref
import de.ph1b.audiobook.playback.utils.ChangeNotifier
import io.reactivex.disposables.Disposable
import javax.inject.Inject
import javax.inject.Named

/**
 * Notifies about changes upon android auto connection.
 */
@PerService
class NotifyOnAutoConnectionChange @Inject constructor(
    private val changeNotifier: ChangeNotifier,
    private val repo: BookRepository,
    @Named(PrefKeys.CURRENT_BOOK)
    private val currentBookIdPref: Pref<Long>,
    private val autoConnection: AndroidAutoConnectedReceiver
) {

  private var listeningDisposable: Disposable? = null

  fun listen() {
    if (listeningDisposable?.isDisposed != false) {
      listeningDisposable = autoConnection.stream
          .filter { it }
          .subscribe {
            // display the current book but don't play it
            repo.bookById(currentBookIdPref.value)?.let {
              changeNotifier.notify(ChangeNotifier.Type.METADATA, it, true)
            }
          }
    }
  }

  fun unregister() {
    listeningDisposable?.dispose()
  }
}