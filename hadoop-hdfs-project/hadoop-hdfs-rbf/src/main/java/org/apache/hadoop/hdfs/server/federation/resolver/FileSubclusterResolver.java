begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.resolver
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
name|federation
operator|.
name|resolver
package|;
end_package

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
name|util
operator|.
name|List
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
import|;
end_import

begin_comment
comment|/**  * Interface to map a file path in the global name space to a specific  * subcluster and path in an HDFS name space.  *<p>  * Each path in the global/federated namespace may map to 1-N different HDFS  * locations.  Each location specifies a single nameservice and a single HDFS  * path.  The behavior is similar to MergeFS and Nfly and allows the merger  * of multiple HDFS locations into a single path.  See HADOOP-8298 and  * HADOOP-12077  *<p>  * For example, a directory listing will fetch listings for each destination  * path and combine them into a single set of results.  *<p>  * When multiple destinations are available for a path, the destinations are  * prioritized in a consistent manner.  This allows the proxy server to  * guess the best/most likely destination and attempt it first.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|FileSubclusterResolver
specifier|public
interface|interface
name|FileSubclusterResolver
block|{
comment|/**    * Get the destinations for a global path. Results are from the mount table    * cache.  If multiple destinations are available, the first result is the    * highest priority destination.    *    * @param path Global path.    * @return Location in a destination namespace or null if it does not exist.    * @throws IOException Throws exception if the data is not available.    */
DECL|method|getDestinationForPath (String path)
name|PathLocation
name|getDestinationForPath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get a list of mount points for a path. Results are from the mount table    * cache.    *    * @param path Path to get the mount points under.    * @return List of mount points present at this path. Return zero-length    *         list if the path is a mount point but there are no mount points    *         under the path. Return null if the path is not a mount point    *         and there are no mount points under the path.    * @throws IOException Throws exception if the data is not available.    */
DECL|method|getMountPoints (String path)
name|List
argument_list|<
name|String
argument_list|>
name|getMountPoints
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the default namespace for the cluster.    *    * @return Default namespace identifier.    */
DECL|method|getDefaultNamespace ()
name|String
name|getDefaultNamespace
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

