begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|CommonConfigurationKeys
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
name|ha
operator|.
name|HAAdmin
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
name|ha
operator|.
name|HAServiceTarget
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|DFSUtil
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
name|hdfs
operator|.
name|DFSUtilClient
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
name|hdfs
operator|.
name|HdfsConfiguration
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
name|util
operator|.
name|ToolRunner
import|;
end_import

begin_comment
comment|/**  * Class to extend HAAdmin to do a little bit of HDFS-specific configuration.  */
end_comment

begin_class
DECL|class|DFSHAAdmin
specifier|public
class|class
name|DFSHAAdmin
extends|extends
name|HAAdmin
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|DFSHAAdmin
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nameserviceId
specifier|private
name|String
name|nameserviceId
decl_stmt|;
DECL|method|setErrOut (PrintStream errOut)
specifier|protected
name|void
name|setErrOut
parameter_list|(
name|PrintStream
name|errOut
parameter_list|)
block|{
name|this
operator|.
name|errOut
operator|=
name|errOut
expr_stmt|;
block|}
DECL|method|setOut (PrintStream out)
specifier|protected
name|void
name|setOut
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|conf
operator|=
name|addSecurityConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Add the requisite security principal settings to the given Configuration,    * returning a copy.    * @param conf the original config    * @return a copy with the security settings added    */
DECL|method|addSecurityConfiguration (Configuration conf)
specifier|public
specifier|static
name|Configuration
name|addSecurityConfiguration
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// Make a copy so we don't mutate it. Also use an HdfsConfiguration to
comment|// force loading of hdfs-site.xml.
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|nameNodePrincipal
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using NN principal: "
operator|+
name|nameNodePrincipal
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|CommonConfigurationKeys
operator|.
name|HADOOP_SECURITY_SERVICE_USER_NAME_KEY
argument_list|,
name|nameNodePrincipal
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
comment|/**    * Try to map the given namenode ID to its service address.    */
annotation|@
name|Override
DECL|method|resolveTarget (String nnId)
specifier|protected
name|HAServiceTarget
name|resolveTarget
parameter_list|(
name|String
name|nnId
parameter_list|)
block|{
name|HdfsConfiguration
name|conf
init|=
operator|(
name|HdfsConfiguration
operator|)
name|getConf
argument_list|()
decl_stmt|;
return|return
operator|new
name|NNHAServiceTarget
argument_list|(
name|conf
argument_list|,
name|nameserviceId
argument_list|,
name|nnId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getUsageString ()
specifier|protected
name|String
name|getUsageString
parameter_list|()
block|{
return|return
literal|"Usage: haadmin [-ns<nameserviceId>]"
return|;
block|}
annotation|@
name|Override
DECL|method|runCmd (String[] argv)
specifier|protected
name|int
name|runCmd
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|argv
operator|.
name|length
operator|<
literal|1
condition|)
block|{
name|printUsage
argument_list|(
name|errOut
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
name|String
name|cmd
init|=
name|argv
index|[
name|i
operator|++
index|]
decl_stmt|;
if|if
condition|(
literal|"-ns"
operator|.
name|equals
argument_list|(
name|cmd
argument_list|)
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|argv
operator|.
name|length
condition|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"Missing nameservice ID"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|nameserviceId
operator|=
name|argv
index|[
name|i
operator|++
index|]
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|argv
operator|.
name|length
condition|)
block|{
name|errOut
operator|.
name|println
argument_list|(
literal|"Missing command"
argument_list|)
expr_stmt|;
name|printUsage
argument_list|(
name|errOut
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
name|argv
operator|=
name|Arrays
operator|.
name|copyOfRange
argument_list|(
name|argv
argument_list|,
name|i
argument_list|,
name|argv
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
return|return
name|super
operator|.
name|runCmd
argument_list|(
name|argv
argument_list|)
return|;
block|}
comment|/**    * returns the list of all namenode ids for the given configuration     */
annotation|@
name|Override
DECL|method|getTargetIds (String namenodeToActivate)
specifier|protected
name|Collection
argument_list|<
name|String
argument_list|>
name|getTargetIds
parameter_list|(
name|String
name|namenodeToActivate
parameter_list|)
block|{
return|return
name|DFSUtilClient
operator|.
name|getNameNodeIds
argument_list|(
name|getConf
argument_list|()
argument_list|,
operator|(
name|nameserviceId
operator|!=
literal|null
operator|)
condition|?
name|nameserviceId
else|:
name|DFSUtil
operator|.
name|getNamenodeNameServiceId
argument_list|(
name|getConf
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
DECL|method|main (String[] argv)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|argv
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|res
init|=
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|DFSHAAdmin
argument_list|()
argument_list|,
name|argv
argument_list|)
decl_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

