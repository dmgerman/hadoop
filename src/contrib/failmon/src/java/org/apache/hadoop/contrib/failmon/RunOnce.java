begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.failmon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|failmon
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/********************************************************** * Runs a set of monitoring jobs once for the local node. The set of * jobs to be run is the intersection of the jobs specifed in the * configuration file and the set of jobs specified in the --only * command line argument.  **********************************************************/
end_comment

begin_class
DECL|class|RunOnce
specifier|public
class|class
name|RunOnce
block|{
DECL|field|lstore
name|LocalStore
name|lstore
decl_stmt|;
DECL|field|monitors
name|ArrayList
argument_list|<
name|MonitorJob
argument_list|>
name|monitors
decl_stmt|;
DECL|field|uploading
name|boolean
name|uploading
init|=
literal|true
decl_stmt|;
DECL|method|RunOnce (String confFile)
specifier|public
name|RunOnce
parameter_list|(
name|String
name|confFile
parameter_list|)
block|{
name|Environment
operator|.
name|prepare
argument_list|(
name|confFile
argument_list|)
expr_stmt|;
name|String
name|localTmpDir
decl_stmt|;
comment|// running as a stand-alone application
name|localTmpDir
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
expr_stmt|;
name|Environment
operator|.
name|setProperty
argument_list|(
literal|"local.tmp.dir"
argument_list|,
name|localTmpDir
argument_list|)
expr_stmt|;
name|monitors
operator|=
name|Environment
operator|.
name|getJobs
argument_list|()
expr_stmt|;
name|lstore
operator|=
operator|new
name|LocalStore
argument_list|()
expr_stmt|;
name|uploading
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|filter (String [] ftypes)
specifier|private
name|void
name|filter
parameter_list|(
name|String
index|[]
name|ftypes
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|MonitorJob
argument_list|>
name|filtered
init|=
operator|new
name|ArrayList
argument_list|<
name|MonitorJob
argument_list|>
argument_list|()
decl_stmt|;
name|boolean
name|found
decl_stmt|;
comment|// filter out unwanted monitor jobs
for|for
control|(
name|MonitorJob
name|job
range|:
name|monitors
control|)
block|{
name|found
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|String
name|ftype
range|:
name|ftypes
control|)
if|if
condition|(
name|job
operator|.
name|type
operator|.
name|equalsIgnoreCase
argument_list|(
name|ftype
argument_list|)
condition|)
name|found
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|found
condition|)
name|filtered
operator|.
name|add
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
comment|// disable uploading if not requested
name|found
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|String
name|ftype
range|:
name|ftypes
control|)
if|if
condition|(
name|ftype
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"upload"
argument_list|)
condition|)
name|found
operator|=
literal|true
expr_stmt|;
if|if
condition|(
operator|!
name|found
condition|)
name|uploading
operator|=
literal|false
expr_stmt|;
name|monitors
operator|=
name|filtered
expr_stmt|;
block|}
DECL|method|run ()
specifier|private
name|void
name|run
parameter_list|()
block|{
name|Environment
operator|.
name|logInfo
argument_list|(
literal|"Failmon started successfully."
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|monitors
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Environment
operator|.
name|logInfo
argument_list|(
literal|"Calling "
operator|+
name|monitors
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|job
operator|.
name|getInfo
argument_list|()
operator|+
literal|"...\t"
argument_list|)
expr_stmt|;
name|monitors
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|job
operator|.
name|monitor
argument_list|(
name|lstore
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|uploading
condition|)
name|lstore
operator|.
name|upload
argument_list|()
expr_stmt|;
name|lstore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|cleanup ()
specifier|public
name|void
name|cleanup
parameter_list|()
block|{
comment|// nothing to be done
block|}
DECL|method|main (String [] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|configFilePath
init|=
literal|"./conf/failmon.properties"
decl_stmt|;
name|String
index|[]
name|onlyList
init|=
literal|null
decl_stmt|;
comment|// Parse command-line parameters
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"--config"
argument_list|)
condition|)
name|configFilePath
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
expr_stmt|;
elseif|else
if|if
condition|(
name|args
index|[
name|i
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"--only"
argument_list|)
condition|)
name|onlyList
operator|=
name|args
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|split
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|RunOnce
name|ro
init|=
operator|new
name|RunOnce
argument_list|(
name|configFilePath
argument_list|)
decl_stmt|;
comment|// only keep the requested types of jobs
if|if
condition|(
name|onlyList
operator|!=
literal|null
condition|)
name|ro
operator|.
name|filter
argument_list|(
name|onlyList
argument_list|)
expr_stmt|;
comment|// run once only
name|ro
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

