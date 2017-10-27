begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.sls.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|sls
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|conf
operator|.
name|Configuration
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
name|net
operator|.
name|NodeBase
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
name|tools
operator|.
name|rumen
operator|.
name|JobTraceReader
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
name|tools
operator|.
name|rumen
operator|.
name|LoggedJob
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
name|tools
operator|.
name|rumen
operator|.
name|LoggedTask
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
name|tools
operator|.
name|rumen
operator|.
name|LoggedTaskAttempt
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
name|sls
operator|.
name|conf
operator|.
name|SLSConfiguration
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SLSUtils
specifier|public
class|class
name|SLSUtils
block|{
DECL|field|DEFAULT_JOB_TYPE
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_JOB_TYPE
init|=
literal|"mapreduce"
decl_stmt|;
comment|// hostname includes the network path and the host name. for example
comment|// "/default-rack/hostFoo" or "/coreSwitchA/TORSwitchB/hostBar".
comment|// the function returns two Strings, the first element is the network
comment|// location without "/", the second element is the host name. for example,
comment|// {"default-rack", "hostFoo"} or "coreSwitchA/TORSwitchB", "hostBar"
DECL|method|getRackHostName (String hostname)
specifier|public
specifier|static
name|String
index|[]
name|getRackHostName
parameter_list|(
name|String
name|hostname
parameter_list|)
block|{
name|NodeBase
name|node
init|=
operator|new
name|NodeBase
argument_list|(
name|hostname
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
index|[]
block|{
name|node
operator|.
name|getNetworkLocation
argument_list|()
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
block|,
name|node
operator|.
name|getName
argument_list|()
block|}
return|;
block|}
comment|/**    * parse the rumen trace file, return each host name    */
DECL|method|parseNodesFromRumenTrace (String jobTrace)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|parseNodesFromRumenTrace
parameter_list|(
name|String
name|jobTrace
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nodeSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|File
name|fin
init|=
operator|new
name|File
argument_list|(
name|jobTrace
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.defaultFS"
argument_list|,
literal|"file:///"
argument_list|)
expr_stmt|;
name|JobTraceReader
name|reader
init|=
operator|new
name|JobTraceReader
argument_list|(
operator|new
name|Path
argument_list|(
name|fin
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
try|try
block|{
name|LoggedJob
name|job
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|job
operator|=
name|reader
operator|.
name|getNext
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|LoggedTask
name|mapTask
range|:
name|job
operator|.
name|getMapTasks
argument_list|()
control|)
block|{
comment|// select the last attempt
if|if
condition|(
name|mapTask
operator|.
name|getAttempts
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|LoggedTaskAttempt
name|taskAttempt
init|=
name|mapTask
operator|.
name|getAttempts
argument_list|()
operator|.
name|get
argument_list|(
name|mapTask
operator|.
name|getAttempts
argument_list|()
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|nodeSet
operator|.
name|add
argument_list|(
name|taskAttempt
operator|.
name|getHostName
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|LoggedTask
name|reduceTask
range|:
name|job
operator|.
name|getReduceTasks
argument_list|()
control|)
block|{
if|if
condition|(
name|reduceTask
operator|.
name|getAttempts
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
continue|continue;
block|}
name|LoggedTaskAttempt
name|taskAttempt
init|=
name|reduceTask
operator|.
name|getAttempts
argument_list|()
operator|.
name|get
argument_list|(
name|reduceTask
operator|.
name|getAttempts
argument_list|()
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|nodeSet
operator|.
name|add
argument_list|(
name|taskAttempt
operator|.
name|getHostName
argument_list|()
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|nodeSet
return|;
block|}
comment|/**    * parse the sls trace file, return each host name    */
DECL|method|parseNodesFromSLSTrace (String jobTrace)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|parseNodesFromSLSTrace
parameter_list|(
name|String
name|jobTrace
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nodeSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|JsonFactory
name|jsonF
init|=
operator|new
name|JsonFactory
argument_list|()
decl_stmt|;
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|Reader
name|input
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|jobTrace
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|Map
argument_list|>
name|i
init|=
name|mapper
operator|.
name|readValues
argument_list|(
name|jsonF
operator|.
name|createParser
argument_list|(
name|input
argument_list|)
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|addNodes
argument_list|(
name|nodeSet
argument_list|,
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|nodeSet
return|;
block|}
DECL|method|addNodes (Set<String> nodeSet, Map jsonEntry)
specifier|private
specifier|static
name|void
name|addNodes
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|nodeSet
parameter_list|,
name|Map
name|jsonEntry
parameter_list|)
block|{
if|if
condition|(
name|jsonEntry
operator|.
name|containsKey
argument_list|(
name|SLSConfiguration
operator|.
name|NUM_NODES
argument_list|)
condition|)
block|{
name|int
name|numNodes
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|jsonEntry
operator|.
name|get
argument_list|(
name|SLSConfiguration
operator|.
name|NUM_NODES
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numRacks
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|jsonEntry
operator|.
name|containsKey
argument_list|(
name|SLSConfiguration
operator|.
name|NUM_RACKS
argument_list|)
condition|)
block|{
name|numRacks
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|jsonEntry
operator|.
name|get
argument_list|(
name|SLSConfiguration
operator|.
name|NUM_RACKS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nodeSet
operator|.
name|addAll
argument_list|(
name|generateNodes
argument_list|(
name|numNodes
argument_list|,
name|numRacks
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|jsonEntry
operator|.
name|containsKey
argument_list|(
name|SLSConfiguration
operator|.
name|JOB_TASKS
argument_list|)
condition|)
block|{
name|List
name|tasks
init|=
operator|(
name|List
operator|)
name|jsonEntry
operator|.
name|get
argument_list|(
name|SLSConfiguration
operator|.
name|JOB_TASKS
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|tasks
control|)
block|{
name|Map
name|jsonTask
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
name|String
name|hostname
init|=
operator|(
name|String
operator|)
name|jsonTask
operator|.
name|get
argument_list|(
name|SLSConfiguration
operator|.
name|TASK_HOST
argument_list|)
decl_stmt|;
if|if
condition|(
name|hostname
operator|!=
literal|null
condition|)
block|{
name|nodeSet
operator|.
name|add
argument_list|(
name|hostname
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * parse the input node file, return each host name    */
DECL|method|parseNodesFromNodeFile (String nodeFile)
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|parseNodesFromNodeFile
parameter_list|(
name|String
name|nodeFile
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nodeSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|JsonFactory
name|jsonF
init|=
operator|new
name|JsonFactory
argument_list|()
decl_stmt|;
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
decl_stmt|;
name|Reader
name|input
init|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|nodeFile
argument_list|)
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
try|try
block|{
name|Iterator
argument_list|<
name|Map
argument_list|>
name|i
init|=
name|mapper
operator|.
name|readValues
argument_list|(
name|jsonF
operator|.
name|createParser
argument_list|(
name|input
argument_list|)
argument_list|,
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Map
name|jsonE
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|rack
init|=
literal|"/"
operator|+
name|jsonE
operator|.
name|get
argument_list|(
literal|"rack"
argument_list|)
decl_stmt|;
name|List
name|tasks
init|=
operator|(
name|List
operator|)
name|jsonE
operator|.
name|get
argument_list|(
literal|"nodes"
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|tasks
control|)
block|{
name|Map
name|jsonNode
init|=
operator|(
name|Map
operator|)
name|o
decl_stmt|;
name|nodeSet
operator|.
name|add
argument_list|(
name|rack
operator|+
literal|"/"
operator|+
name|jsonNode
operator|.
name|get
argument_list|(
literal|"node"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|nodeSet
return|;
block|}
DECL|method|generateNodes (int numNodes, int numRacks)
specifier|public
specifier|static
name|Set
argument_list|<
name|?
extends|extends
name|String
argument_list|>
name|generateNodes
parameter_list|(
name|int
name|numNodes
parameter_list|,
name|int
name|numRacks
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|nodeSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|numRacks
operator|<
literal|1
condition|)
block|{
name|numRacks
operator|=
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|numRacks
operator|>
name|numNodes
condition|)
block|{
name|numRacks
operator|=
name|numNodes
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numNodes
condition|;
name|i
operator|++
control|)
block|{
name|nodeSet
operator|.
name|add
argument_list|(
literal|"/rack"
operator|+
name|i
operator|%
name|numRacks
operator|+
literal|"/node"
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|nodeSet
return|;
block|}
block|}
end_class

end_unit

