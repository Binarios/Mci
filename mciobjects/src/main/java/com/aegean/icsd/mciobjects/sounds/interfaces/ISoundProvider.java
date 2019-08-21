package com.aegean.icsd.mciobjects.sounds.interfaces;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.sounds.beans.Sound;
import com.aegean.icsd.mciobjects.words.beans.Word;

public interface ISoundProvider {
  Sound selectRandomSoundWithSubject(Word word) throws ProviderException;
}
