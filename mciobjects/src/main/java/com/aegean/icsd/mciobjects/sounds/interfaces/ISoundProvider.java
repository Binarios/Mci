package com.aegean.icsd.mciobjects.sounds.interfaces;

import java.util.List;

import com.aegean.icsd.mciobjects.common.beans.ProviderException;
import com.aegean.icsd.mciobjects.images.beans.Image;
import com.aegean.icsd.mciobjects.sounds.beans.Sound;
import com.aegean.icsd.mciobjects.words.beans.Word;

public interface ISoundProvider {
  List<Sound> getNewSoundsFor(String entityName, int count, Sound criteria) throws ProviderException;

  Sound selectRandomSoundWithSubject(Word word) throws ProviderException;

  List<Sound> selectSoundsByEntityId(String entityId) throws ProviderException;

  boolean isAssociatedWithImage(Sound sound, Image image) throws ProviderException;
}
