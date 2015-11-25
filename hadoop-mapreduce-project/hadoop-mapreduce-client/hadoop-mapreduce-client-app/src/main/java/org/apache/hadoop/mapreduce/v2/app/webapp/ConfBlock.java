begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|AMParams
operator|.
name|JOB_ID
import|;
end_import

begin_import
import|import static
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
name|JQueryUI
operator|.
name|_TH
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|fs
operator|.
name|Path
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
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ConfEntryInfo
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
name|v2
operator|.
name|app
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ConfInfo
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
name|v2
operator|.
name|util
operator|.
name|MRApps
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
name|yarn
operator|.
name|webapp
operator|.
name|hamlet
operator|.
name|Hamlet
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
name|yarn
operator|.
name|webapp
operator|.
name|hamlet
operator|.
name|Hamlet
operator|.
name|TABLE
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
name|yarn
operator|.
name|webapp
operator|.
name|hamlet
operator|.
name|Hamlet
operator|.
name|TBODY
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
name|yarn
operator|.
name|webapp
operator|.
name|hamlet
operator|.
name|HamletSpec
operator|.
name|InputType
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
name|yarn
operator|.
name|webapp
operator|.
name|view
operator|.
name|HtmlBlock
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_comment
comment|/**  * Render the configuration for this job.  */
end_comment

begin_class
DECL|class|ConfBlock
specifier|public
class|class
name|ConfBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|appContext
specifier|final
name|AppContext
name|appContext
decl_stmt|;
DECL|method|ConfBlock (AppContext appctx)
annotation|@
name|Inject
name|ConfBlock
parameter_list|(
name|AppContext
name|appctx
parameter_list|)
block|{
name|appContext
operator|=
name|appctx
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    * @see org.apache.hadoop.yarn.webapp.view.HtmlBlock#render(org.apache.hadoop.yarn.webapp.view.HtmlBlock.Block)    */
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
name|String
name|jid
init|=
name|$
argument_list|(
name|JOB_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|jid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|html
operator|.
name|p
argument_list|()
operator|.
name|_
argument_list|(
literal|"Sorry, can't do anything without a JobID."
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return;
block|}
name|JobId
name|jobID
init|=
name|MRApps
operator|.
name|toJobID
argument_list|(
name|jid
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|appContext
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
if|if
condition|(
name|job
operator|==
literal|null
condition|)
block|{
name|html
operator|.
name|p
argument_list|()
operator|.
name|_
argument_list|(
literal|"Sorry, "
argument_list|,
name|jid
argument_list|,
literal|" not found."
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
return|return;
block|}
name|Path
name|confPath
init|=
name|job
operator|.
name|getConfFile
argument_list|()
decl_stmt|;
try|try
block|{
name|ConfInfo
name|info
init|=
operator|new
name|ConfInfo
argument_list|(
name|job
argument_list|)
decl_stmt|;
name|html
operator|.
name|div
argument_list|()
operator|.
name|h3
argument_list|(
name|confPath
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|TBODY
argument_list|<
name|TABLE
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|tbody
init|=
name|html
operator|.
comment|// Tasks table
name|table
argument_list|(
literal|"#conf"
argument_list|)
operator|.
name|thead
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"key"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"value"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"source chain"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|tbody
argument_list|()
decl_stmt|;
for|for
control|(
name|ConfEntryInfo
name|entry
range|:
name|info
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
index|[]
name|sources
init|=
name|entry
operator|.
name|getSource
argument_list|()
decl_stmt|;
comment|//Skip the last entry, because it is always the same HDFS file, and
comment|// output them in reverse order so most recent is output first
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
operator|(
name|sources
operator|.
name|length
operator|-
literal|2
operator|)
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"<- "
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|sources
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|tbody
operator|.
name|tr
argument_list|()
operator|.
name|td
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|td
argument_list|(
name|buffer
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|tbody
operator|.
name|_
argument_list|()
operator|.
name|tfoot
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"key"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"key"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"value"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"value"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|th
argument_list|()
operator|.
name|input
argument_list|(
literal|"search_init"
argument_list|)
operator|.
name|$type
argument_list|(
name|InputType
operator|.
name|text
argument_list|)
operator|.
name|$name
argument_list|(
literal|"source chain"
argument_list|)
operator|.
name|$value
argument_list|(
literal|"source chain"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while reading "
operator|+
name|confPath
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|html
operator|.
name|p
argument_list|()
operator|.
name|_
argument_list|(
literal|"Sorry got an error while reading conf file. "
argument_list|,
name|confPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

