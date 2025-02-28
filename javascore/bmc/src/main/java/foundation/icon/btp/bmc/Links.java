/*
 * Copyright 2021 ICON Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package foundation.icon.btp.bmc;

import foundation.icon.score.data.EnumerableDictDB;
import foundation.icon.score.util.Logger;

import java.util.Map;

public class Links extends EnumerableDictDB<String, Link> {
    private static final Logger logger = Logger.getLogger(Links.class);

    public Links(String id) {
        super(id, String.class, Link.class);
    }

    public Link ensureRelays(Link link) {
        if (link != null && link.getRelays() == null) {
            String linkId = super.concatId(link.getAddr());
            link.setRelays(new Relays(concatId(linkId, "relays")));
        }
        return link;
    }

    @Override
    public Link get(String key) {
        return ensureRelays(super.get(key));
    }

    @Override
    public Link put(String key, Link value) {
        return ensureRelays(super.put(key, value));
    }

    @Override
    public Link remove(String key) {
        return ensureRelays(super.remove(key));
    }

    @Override
    public Map<String, Link> toMap() {
        Map<String, Link> map = super.toMap();
        for(Map.Entry<String, Link> entry : map.entrySet()) {
            ensureRelays(entry.getValue());
        }
        return map;
    }
}
