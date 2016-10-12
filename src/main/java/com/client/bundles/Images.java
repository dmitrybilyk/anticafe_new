package com.client.bundles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Images extends ClientBundle {
  public static final Images INSTANCE =  GWT.create(Images.class);

   @Source("images/progress.gif")
   ImageResource progress();

   @Source("images/broom.gif")
   ImageResource removedSession();

   @Source("images/created_session.gif")
   ImageResource stopped();

 @Source("images/unlimited.gif")
 ImageResource stoppedUnlimited();

 @Source("images/created_session.gif")
 ImageResource createdSession();

 @Source("images/animated-dollar-sign-gif.gif")
 ImageResource payedSession();

 }