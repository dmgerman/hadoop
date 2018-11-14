begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocol
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|ha
operator|.
name|ReadOnly
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|HashSet
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Testing class for {@link ReadOnly} annotation on {@link ClientProtocol}.  */
end_comment

begin_class
DECL|class|TestReadOnly
specifier|public
class|class
name|TestReadOnly
block|{
DECL|field|ALL_METHODS
specifier|private
specifier|static
specifier|final
name|Method
index|[]
name|ALL_METHODS
init|=
name|ClientProtocol
operator|.
name|class
operator|.
name|getMethods
argument_list|()
decl_stmt|;
DECL|field|READONLY_METHOD_NAMES
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|READONLY_METHOD_NAMES
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"getBlockLocations"
argument_list|,
literal|"getServerDefaults"
argument_list|,
literal|"getStoragePolicies"
argument_list|,
literal|"getStoragePolicy"
argument_list|,
literal|"getListing"
argument_list|,
literal|"getSnapshottableDirListing"
argument_list|,
literal|"getPreferredBlockSize"
argument_list|,
literal|"listCorruptFileBlocks"
argument_list|,
literal|"getFileInfo"
argument_list|,
literal|"isFileClosed"
argument_list|,
literal|"getFileLinkInfo"
argument_list|,
literal|"getLocatedFileInfo"
argument_list|,
literal|"getContentSummary"
argument_list|,
literal|"getLinkTarget"
argument_list|,
literal|"getSnapshotDiffReport"
argument_list|,
literal|"getSnapshotDiffReportListing"
argument_list|,
literal|"listCacheDirectives"
argument_list|,
literal|"listCachePools"
argument_list|,
literal|"getAclStatus"
argument_list|,
literal|"getEZForPath"
argument_list|,
literal|"listEncryptionZones"
argument_list|,
literal|"listReencryptionStatus"
argument_list|,
literal|"getXAttrs"
argument_list|,
literal|"listXAttrs"
argument_list|,
literal|"checkAccess"
argument_list|,
literal|"getErasureCodingPolicies"
argument_list|,
literal|"getErasureCodingCodecs"
argument_list|,
literal|"getErasureCodingPolicy"
argument_list|,
literal|"listOpenFiles"
argument_list|,
literal|"getStats"
argument_list|,
literal|"getReplicatedBlockStats"
argument_list|,
literal|"getECBlockGroupStats"
argument_list|,
literal|"getDatanodeReport"
argument_list|,
literal|"getDatanodeStorageReport"
argument_list|,
literal|"getDataEncryptionKey"
argument_list|,
literal|"getCurrentEditLogTxid"
argument_list|,
literal|"getEditsFromTxid"
argument_list|,
literal|"getQuotaUsage"
argument_list|,
literal|"msync"
argument_list|,
literal|"getHAServiceState"
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testReadOnly ()
specifier|public
name|void
name|testReadOnly
parameter_list|()
block|{
for|for
control|(
name|Method
name|m
range|:
name|ALL_METHODS
control|)
block|{
name|boolean
name|expected
init|=
name|READONLY_METHOD_NAMES
operator|.
name|contains
argument_list|(
name|m
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|checkIsReadOnly
argument_list|(
name|m
operator|.
name|getName
argument_list|()
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkIsReadOnly (String methodName, boolean expected)
specifier|private
name|void
name|checkIsReadOnly
parameter_list|(
name|String
name|methodName
parameter_list|,
name|boolean
name|expected
parameter_list|)
block|{
for|for
control|(
name|Method
name|m
range|:
name|ALL_METHODS
control|)
block|{
comment|// Note here we only check the FIRST result of overloaded methods
comment|// with the same name. The assumption is that all these methods should
comment|// share the same annotation.
if|if
condition|(
name|m
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|methodName
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"Expected ReadOnly for method '"
operator|+
name|methodName
operator|+
literal|"' to be "
operator|+
name|expected
argument_list|,
name|m
operator|.
name|isAnnotationPresent
argument_list|(
name|ReadOnly
operator|.
name|class
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown method name: "
operator|+
name|methodName
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

