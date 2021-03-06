begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.rumen
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|rumen
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Queue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Counters
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|jobhistory
operator|.
name|HistoryEvent
import|;
end_import

begin_class
DECL|class|HistoryEventEmitter
specifier|abstract
class|class
name|HistoryEventEmitter
block|{
DECL|field|LOG
specifier|static
specifier|final
specifier|private
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|HistoryEventEmitter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|nonFinalSEEs ()
specifier|abstract
name|List
argument_list|<
name|SingleEventEmitter
argument_list|>
name|nonFinalSEEs
parameter_list|()
function_decl|;
DECL|method|finalSEEs ()
specifier|abstract
name|List
argument_list|<
name|SingleEventEmitter
argument_list|>
name|finalSEEs
parameter_list|()
function_decl|;
DECL|method|HistoryEventEmitter ()
specifier|protected
name|HistoryEventEmitter
parameter_list|()
block|{
comment|// no code
block|}
DECL|enum|PostEmitAction
enum|enum
name|PostEmitAction
block|{
DECL|enumConstant|NONE
DECL|enumConstant|REMOVE_HEE
name|NONE
block|,
name|REMOVE_HEE
block|}
empty_stmt|;
DECL|method|emitterCore (ParsedLine line, String name)
specifier|final
name|Pair
argument_list|<
name|Queue
argument_list|<
name|HistoryEvent
argument_list|>
argument_list|,
name|PostEmitAction
argument_list|>
name|emitterCore
parameter_list|(
name|ParsedLine
name|line
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Queue
argument_list|<
name|HistoryEvent
argument_list|>
name|results
init|=
operator|new
name|LinkedList
argument_list|<
name|HistoryEvent
argument_list|>
argument_list|()
decl_stmt|;
name|PostEmitAction
name|removeEmitter
init|=
name|PostEmitAction
operator|.
name|NONE
decl_stmt|;
for|for
control|(
name|SingleEventEmitter
name|see
range|:
name|nonFinalSEEs
argument_list|()
control|)
block|{
name|HistoryEvent
name|event
init|=
name|see
operator|.
name|maybeEmitEvent
argument_list|(
name|line
argument_list|,
name|name
argument_list|,
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|event
operator|!=
literal|null
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|SingleEventEmitter
name|see
range|:
name|finalSEEs
argument_list|()
control|)
block|{
name|HistoryEvent
name|event
init|=
name|see
operator|.
name|maybeEmitEvent
argument_list|(
name|line
argument_list|,
name|name
argument_list|,
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|event
operator|!=
literal|null
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|event
argument_list|)
expr_stmt|;
name|removeEmitter
operator|=
name|PostEmitAction
operator|.
name|REMOVE_HEE
expr_stmt|;
break|break;
block|}
block|}
return|return
operator|new
name|Pair
argument_list|<
name|Queue
argument_list|<
name|HistoryEvent
argument_list|>
argument_list|,
name|PostEmitAction
argument_list|>
argument_list|(
name|results
argument_list|,
name|removeEmitter
argument_list|)
return|;
block|}
DECL|method|maybeParseCounters (String counters)
specifier|protected
specifier|static
name|Counters
name|maybeParseCounters
parameter_list|(
name|String
name|counters
parameter_list|)
block|{
try|try
block|{
return|return
name|parseCounters
argument_list|(
name|counters
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"The counter string, \""
operator|+
name|counters
operator|+
literal|"\" is badly formatted."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|parseCounters (String counters)
specifier|protected
specifier|static
name|Counters
name|parseCounters
parameter_list|(
name|String
name|counters
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|counters
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"HistoryEventEmitters: null counter detected:"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|counters
operator|=
name|counters
operator|.
name|replace
argument_list|(
literal|"\\."
argument_list|,
literal|"\\\\."
argument_list|)
expr_stmt|;
name|counters
operator|=
name|counters
operator|.
name|replace
argument_list|(
literal|"\\\\{"
argument_list|,
literal|"\\{"
argument_list|)
expr_stmt|;
name|counters
operator|=
name|counters
operator|.
name|replace
argument_list|(
literal|"\\\\}"
argument_list|,
literal|"\\}"
argument_list|)
expr_stmt|;
name|counters
operator|=
name|counters
operator|.
name|replace
argument_list|(
literal|"\\\\("
argument_list|,
literal|"\\("
argument_list|)
expr_stmt|;
name|counters
operator|=
name|counters
operator|.
name|replace
argument_list|(
literal|"\\\\)"
argument_list|,
literal|"\\)"
argument_list|)
expr_stmt|;
name|counters
operator|=
name|counters
operator|.
name|replace
argument_list|(
literal|"\\\\["
argument_list|,
literal|"\\["
argument_list|)
expr_stmt|;
name|counters
operator|=
name|counters
operator|.
name|replace
argument_list|(
literal|"\\\\]"
argument_list|,
literal|"\\]"
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Counters
name|depForm
init|=
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Counters
operator|.
name|fromEscapedCompactString
argument_list|(
name|counters
argument_list|)
decl_stmt|;
return|return
operator|new
name|Counters
argument_list|(
name|depForm
argument_list|)
return|;
block|}
block|}
end_class

end_unit

