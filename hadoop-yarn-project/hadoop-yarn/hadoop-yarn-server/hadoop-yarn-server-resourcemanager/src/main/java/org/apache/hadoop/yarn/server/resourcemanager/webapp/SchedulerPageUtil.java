begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|resourcemanager
operator|.
name|webapp
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|HtmlBlock
import|;
end_import

begin_class
DECL|class|SchedulerPageUtil
specifier|public
class|class
name|SchedulerPageUtil
block|{
DECL|class|QueueBlockUtil
specifier|static
class|class
name|QueueBlockUtil
extends|extends
name|HtmlBlock
block|{
DECL|method|reopenQueue (Block html)
specifier|private
name|void
name|reopenQueue
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|html
operator|.
name|script
argument_list|()
operator|.
name|$type
argument_list|(
literal|"text/javascript"
argument_list|)
operator|.
name|__
argument_list|(
literal|"function reopenQueryNodes() {"
argument_list|,
literal|"  var currentParam = decodeURIComponent(window.location.href)"
operator|+
literal|".split('?');"
argument_list|,
literal|"  var tmpCurrentParam = currentParam;"
argument_list|,
literal|"  var queryQueuesString = '';"
argument_list|,
literal|"  if (tmpCurrentParam.length> 1) {"
argument_list|,
literal|"    // openQueues=q1#q2&param1=value1&param2=value2"
argument_list|,
literal|"    tmpCurrentParam = tmpCurrentParam[1];"
argument_list|,
literal|"    if (tmpCurrentParam.indexOf('openQueues=') != -1 ) {"
argument_list|,
literal|"      tmpCurrentParam = tmpCurrentParam.split('openQueues=')[1].split('&')[0];"
argument_list|,
literal|"      queryQueuesString = tmpCurrentParam;"
argument_list|,
literal|"    }"
argument_list|,
literal|"  }"
argument_list|,
literal|"  if (queryQueuesString != '') {"
argument_list|,
literal|"    queueArray = queryQueuesString.split('#');"
argument_list|,
literal|"    $('#cs .q').each(function() {"
argument_list|,
literal|"      var name = $(this).html();"
argument_list|,
literal|"      if (name != 'root'&& $.inArray(name, queueArray) != -1) {"
argument_list|,
literal|"        $(this).closest('li').removeClass('jstree-closed').addClass('jstree-open'); "
argument_list|,
literal|"      }"
argument_list|,
literal|"    });"
argument_list|,
literal|"  }"
argument_list|,
literal|"  $('#cs').bind( {"
argument_list|,
literal|"                  'open_node.jstree' :function(e, data) { storeExpandedQueue(e, data); },"
argument_list|,
literal|"                  'close_node.jstree':function(e, data) { storeExpandedQueue(e, data); }"
argument_list|,
literal|"  });"
argument_list|,
literal|"}"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
DECL|method|storeExpandedQueue (Block html)
specifier|private
name|void
name|storeExpandedQueue
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|html
operator|.
name|script
argument_list|()
operator|.
name|$type
argument_list|(
literal|"text/javascript"
argument_list|)
operator|.
name|__
argument_list|(
literal|"function storeExpandedQueue(e, data) {"
argument_list|,
literal|"  var OPEN_QUEUES = 'openQueues';"
argument_list|,
literal|"  var ACTION_OPEN = 'open';"
argument_list|,
literal|"  var ACTION_CLOSED = 'closed';"
argument_list|,
literal|"  var $li = $(data.args[0]);"
argument_list|,
literal|"  var action = ACTION_CLOSED;  //closed or open"
argument_list|,
literal|"  var queueName = ''"
argument_list|,
literal|"  if ($li.hasClass('jstree-open')) {"
argument_list|,
literal|"      action=ACTION_OPEN;"
argument_list|,
literal|"  }"
argument_list|,
literal|"  queueName = $li.find('.q').html();"
argument_list|,
literal|"  // http://localhost:8088/cluster/scheduler?openQueues=q1#q2&param1=value1&param2=value2 "
argument_list|,
literal|"  //   ==> [http://localhost:8088/cluster/scheduler , openQueues=q1#q2&param1=value1&param2=value2]"
argument_list|,
literal|"  var currentParam = window.location.href.split('?');"
argument_list|,
literal|"  var tmpCurrentParam = currentParam;"
argument_list|,
literal|"  var queryString = '';"
argument_list|,
literal|"  if (tmpCurrentParam.length> 1) {"
argument_list|,
literal|"    // openQueues=q1#q2&param1=value1&param2=value2"
argument_list|,
literal|"    tmpCurrentParam = tmpCurrentParam[1];"
argument_list|,
literal|"    currentParam = tmpCurrentParam;"
argument_list|,
literal|"    tmpCurrentParam = tmpCurrentParam.split('&');"
argument_list|,
literal|"    var len = tmpCurrentParam.length;"
argument_list|,
literal|"    var paramExist = false;"
argument_list|,
literal|"    if (len> 1) {    // Currently no query param are present but in future if any are added for that handling it now"
argument_list|,
literal|"      queryString = '';"
argument_list|,
literal|"      for (var i = 0 ; i< len ; i++) {  // searching for param openQueues"
argument_list|,
literal|"        if (tmpCurrentParam[i].substr(0,11) == OPEN_QUEUES + '=') {"
argument_list|,
literal|"          if (action == ACTION_OPEN) {"
argument_list|,
literal|"            tmpCurrentParam[i] = addQueueName(tmpCurrentParam[i],queueName);"
argument_list|,
literal|"          }"
argument_list|,
literal|"          else if (action == ACTION_CLOSED) {"
argument_list|,
literal|"            tmpCurrentParam[i] = removeQueueName(tmpCurrentParam[i] , queueName);"
argument_list|,
literal|"          }"
argument_list|,
literal|"          paramExist = true;"
argument_list|,
literal|"        }"
argument_list|,
literal|"        if (i> 0) {"
argument_list|,
literal|"          queryString += '&';"
argument_list|,
literal|"        }"
argument_list|,
literal|"        queryString += tmpCurrentParam[i];"
argument_list|,
literal|"      }"
argument_list|,
literal|"      // If in existing query string OPEN_QUEUES param is not present"
argument_list|,
literal|"      if (action == ACTION_OPEN&& !paramExist) {"
argument_list|,
literal|"        queryString = currentParam + '&' + OPEN_QUEUES + '=' + queueName;"
argument_list|,
literal|"      }"
argument_list|,
literal|"    } "
argument_list|,
literal|"    // Only one param is present in current query string"
argument_list|,
literal|"    else {"
argument_list|,
literal|"      tmpCurrentParam=tmpCurrentParam[0];"
argument_list|,
literal|"      // checking if the only param present in query string is OPEN_QUEUES or not and making queryString accordingly"
argument_list|,
literal|"      if (tmpCurrentParam.substr(0,11) == OPEN_QUEUES + '=') {"
argument_list|,
literal|"        if (action == ACTION_OPEN) {"
argument_list|,
literal|"          queryString = addQueueName(tmpCurrentParam,queueName);"
argument_list|,
literal|"        }"
argument_list|,
literal|"        else if (action == ACTION_CLOSED) {"
argument_list|,
literal|"          queryString = removeQueueName(tmpCurrentParam , queueName);"
argument_list|,
literal|"        }"
argument_list|,
literal|"      }"
argument_list|,
literal|"      else {"
argument_list|,
literal|"        if (action == ACTION_OPEN) {"
argument_list|,
literal|"          queryString = tmpCurrentParam + '&' + OPEN_QUEUES + '=' + queueName;"
argument_list|,
literal|"        }"
argument_list|,
literal|"      }"
argument_list|,
literal|"    }"
argument_list|,
literal|"  } else {"
argument_list|,
literal|"    if (action == ACTION_OPEN) {"
argument_list|,
literal|"      tmpCurrentParam = '';"
argument_list|,
literal|"      currentParam = tmpCurrentParam;"
argument_list|,
literal|"      queryString = OPEN_QUEUES+'='+queueName;"
argument_list|,
literal|"    }"
argument_list|,
literal|"  }"
argument_list|,
literal|"  if (queryString != '') {"
argument_list|,
literal|"    queryString = '?' + queryString;"
argument_list|,
literal|"  }"
argument_list|,
literal|"  var url = window.location.protocol + '//' + window.location.host + window.location.pathname + queryString;"
argument_list|,
literal|"  window.history.pushState( { path : url }, '', url);"
argument_list|,
literal|"};"
argument_list|,
literal|""
argument_list|,
literal|"function removeQueueName(queryString, queueName) {"
argument_list|,
literal|"  queryString = decodeURIComponent(queryString);"
argument_list|,
literal|"  var index = queryString.indexOf(queueName);"
argument_list|,
literal|"  // Finding if queue is present in query param then only remove it"
argument_list|,
literal|"  if (index != -1) {"
argument_list|,
literal|"    // removing openQueues="
argument_list|,
literal|"    var tmp = queryString.substr(11, queryString.length);"
argument_list|,
literal|"    tmp = tmp.split('#');"
argument_list|,
literal|"    var len = tmp.length;"
argument_list|,
literal|"    var newQueryString = '';"
argument_list|,
literal|"    for (var i = 0 ; i< len ; i++) {"
argument_list|,
literal|"      if (tmp[i] != queueName) {"
argument_list|,
literal|"        if (newQueryString != '') {"
argument_list|,
literal|"          newQueryString += '#';"
argument_list|,
literal|"        }"
argument_list|,
literal|"        newQueryString += tmp[i];"
argument_list|,
literal|"      }"
argument_list|,
literal|"    }"
argument_list|,
literal|"    queryString = newQueryString;"
argument_list|,
literal|"    if (newQueryString != '') {"
argument_list|,
literal|"      queryString = 'openQueues=' + newQueryString;"
argument_list|,
literal|"    }"
argument_list|,
literal|"  }"
argument_list|,
literal|"  return queryString;"
argument_list|,
literal|"}"
argument_list|,
literal|""
argument_list|,
literal|"function addQueueName(queryString, queueName) {"
argument_list|,
literal|"  queueArray = queryString.split('#');"
argument_list|,
literal|"  if ($.inArray(queueArray, queueName) == -1) {"
argument_list|,
literal|"    queryString = queryString + '#' + queueName;"
argument_list|,
literal|"  }"
argument_list|,
literal|"  return queryString;"
argument_list|,
literal|"}"
argument_list|)
operator|.
name|__
argument_list|()
expr_stmt|;
block|}
DECL|method|render (Block html)
annotation|@
name|Override
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|reopenQueue
argument_list|(
name|html
argument_list|)
expr_stmt|;
name|storeExpandedQueue
argument_list|(
name|html
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

