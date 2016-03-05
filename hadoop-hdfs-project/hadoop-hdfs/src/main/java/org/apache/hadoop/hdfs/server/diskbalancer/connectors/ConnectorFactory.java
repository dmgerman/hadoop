begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.diskbalancer.connectors
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|diskbalancer
operator|.
name|connectors
package|;
end_package

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
name|conf
operator|.
name|Configuration
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_comment
comment|/**  * Connector factory creates appropriate connector based on the URL.  */
end_comment

begin_class
DECL|class|ConnectorFactory
specifier|public
specifier|final
class|class
name|ConnectorFactory
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ConnectorFactory
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Constructs an appropriate connector based on the URL.    * @param clusterURI - URL    * @return ClusterConnector    */
DECL|method|getCluster (URI clusterURI, Configuration conf)
specifier|public
specifier|static
name|ClusterConnector
name|getCluster
parameter_list|(
name|URI
name|clusterURI
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cluster URI : {}"
argument_list|,
name|clusterURI
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"scheme : {}"
argument_list|,
name|clusterURI
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|clusterURI
operator|.
name|getScheme
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating a JsonNodeConnector"
argument_list|)
expr_stmt|;
return|return
operator|new
name|JsonNodeConnector
argument_list|(
name|clusterURI
operator|.
name|toURL
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Creating NameNode connector"
argument_list|)
expr_stmt|;
return|return
operator|new
name|DBNameNodeConnector
argument_list|(
name|clusterURI
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
DECL|method|ConnectorFactory ()
specifier|private
name|ConnectorFactory
parameter_list|()
block|{
comment|// never constructed
block|}
block|}
end_class

end_unit

