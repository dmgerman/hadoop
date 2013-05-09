begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
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
name|net
operator|.
name|URI
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
name|FileSystem
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
name|hdfs
operator|.
name|DistributedFileSystem
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
name|protocol
operator|.
name|HdfsConstants
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
name|tools
operator|.
name|DFSAdmin
import|;
end_import

begin_comment
comment|/**  * The public API for performing administrative functions on HDFS. Those writing  * applications against HDFS should prefer this interface to directly accessing  * functionality in DistributedFileSystem or DFSClient.  *   * Note that this is distinct from the similarly-named {@link DFSAdmin}, which  * is a class that provides the functionality for the CLI `hdfs dfsadmin ...'  * commands.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|HdfsAdmin
specifier|public
class|class
name|HdfsAdmin
block|{
DECL|field|dfs
specifier|private
name|DistributedFileSystem
name|dfs
decl_stmt|;
comment|/**    * Create a new HdfsAdmin client.    *     * @param uri the unique URI of the HDFS file system to administer    * @param conf configuration    * @throws IOException in the event the file system could not be created    */
DECL|method|HdfsAdmin (URI uri, Configuration conf)
specifier|public
name|HdfsAdmin
parameter_list|(
name|URI
name|uri
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|uri
argument_list|,
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|fs
operator|instanceof
name|DistributedFileSystem
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"'"
operator|+
name|uri
operator|+
literal|"' is not an HDFS URI."
argument_list|)
throw|;
block|}
else|else
block|{
name|dfs
operator|=
operator|(
name|DistributedFileSystem
operator|)
name|fs
expr_stmt|;
block|}
block|}
comment|/**    * Set the namespace quota (count of files, directories, and sym links) for a    * directory.    *     * @param src the path to set the quota for    * @param quota the value to set for the quota    * @throws IOException in the event of error    */
DECL|method|setQuota (Path src, long quota)
specifier|public
name|void
name|setQuota
parameter_list|(
name|Path
name|src
parameter_list|,
name|long
name|quota
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setQuota
argument_list|(
name|src
argument_list|,
name|quota
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clear the namespace quota (count of files, directories and sym links) for a    * directory.    *     * @param src the path to clear the quota of    * @throws IOException in the event of error    */
DECL|method|clearQuota (Path src)
specifier|public
name|void
name|clearQuota
parameter_list|(
name|Path
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setQuota
argument_list|(
name|src
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_RESET
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the disk space quota (size of files) for a directory. Note that    * directories and sym links do not occupy disk space.    *     * @param src the path to set the space quota of    * @param spaceQuota the value to set for the space quota    * @throws IOException in the event of error    */
DECL|method|setSpaceQuota (Path src, long spaceQuota)
specifier|public
name|void
name|setSpaceQuota
parameter_list|(
name|Path
name|src
parameter_list|,
name|long
name|spaceQuota
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setQuota
argument_list|(
name|src
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|,
name|spaceQuota
argument_list|)
expr_stmt|;
block|}
comment|/**    * Clear the disk space quota (size of files) for a directory. Note that    * directories and sym links do not occupy disk space.    *     * @param src the path to clear the space quota of    * @throws IOException in the event of error    */
DECL|method|clearSpaceQuota (Path src)
specifier|public
name|void
name|clearSpaceQuota
parameter_list|(
name|Path
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|setQuota
argument_list|(
name|src
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_DONT_SET
argument_list|,
name|HdfsConstants
operator|.
name|QUOTA_RESET
argument_list|)
expr_stmt|;
block|}
comment|/**    * Allow snapshot on a directory.    * @param path The path of the directory where snapshots will be taken.    */
DECL|method|allowSnapshot (Path path)
specifier|public
name|void
name|allowSnapshot
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|allowSnapshot
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Disallow snapshot on a directory.    * @param path The path of the snapshottable directory.    */
DECL|method|disallowSnapshot (Path path)
specifier|public
name|void
name|disallowSnapshot
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|dfs
operator|.
name|disallowSnapshot
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

