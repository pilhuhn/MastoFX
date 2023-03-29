/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.bsd.mastofx;

import com.google.gson.annotations.Expose;
import social.bigbone.MastodonClient;

/**
 * @author hrupp
 */
public class Server {
  @Expose String name;
  @Expose(serialize = false) String token;
  @Expose String description;

  @Expose
  Boolean defaultServer = false;

  @Expose(serialize = false, deserialize = false)
  MastodonClient client;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public boolean isAnonymous() {
    return token==null || token.isBlank();
  }
}
