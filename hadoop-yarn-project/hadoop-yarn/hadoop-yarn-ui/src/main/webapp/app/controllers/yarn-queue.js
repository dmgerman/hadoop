/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Ember from 'ember';

export default Ember.Controller.extend({
  needReload: true,
  selectedQueue: undefined,

  breadcrumbs: Ember.computed("model.selected", function () {
    var queueName = this.get("model.selected");

    return [{
      text: "Home",
      routeName: 'application'
    }, {
      text: "Queues",
      routeName: 'yarn-queues',
      model: 'root'
    }, {
      text: `Queue [ ${queueName} ]`,
      routeName: 'yarn-queue',
      model: queueName
    }];

  }),


});
