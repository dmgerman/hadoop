begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.extdataset
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
name|datanode
operator|.
name|extdataset
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
name|datanode
operator|.
name|Replica
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
name|server
operator|.
name|datanode
operator|.
name|ReplicaInPipeline
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsDatasetSpi
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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
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

begin_comment
comment|/**  * Tests the ability to create external FsDatasetSpi implementations.  *  * The purpose of this suite of tests is to ensure that it is possible to  * construct subclasses of FsDatasetSpi outside the Hadoop tree  * (specifically, outside of the org.apache.hadoop.hdfs.server.datanode  * package).  This consists of creating subclasses of the two key classes  * (FsDatasetSpi and FsVolumeSpi) *and* instances or subclasses of any  * classes/interfaces their methods need to produce.  If methods are added  * to or changed in any superclasses, or if constructors of other classes  * are changed, this package will fail to compile.  In fixing this  * compilation error, any new class dependencies should receive the same  * treatment.  *  * It is worth noting what these tests do *not* accomplish.  Just as  * important as being able to produce instances of the appropriate classes  * is being able to access all necessary methods on those classes as well  * as on any additional classes accepted as inputs to FsDatasetSpi's  * methods.  It wouldn't be correct to mandate all methods be public, as  * that would defeat encapsulation.  Moreover, there is no natural  * mechanism that would prevent a manually-constructed list of methods  * from becoming stale.  Rather than creating tests with no clear means of  * maintaining them, this problem is left unsolved for now.  *  * Lastly, though merely compiling this package should signal success,  * explicit testInstantiate* unit tests are included below so as to have a  * tangible means of referring to each case.  */
end_comment

begin_class
DECL|class|TestExternalDataset
specifier|public
class|class
name|TestExternalDataset
block|{
comment|/**    * Tests instantiating an FsDatasetSpi subclass.    */
annotation|@
name|Test
DECL|method|testInstantiateDatasetImpl ()
specifier|public
name|void
name|testInstantiateDatasetImpl
parameter_list|()
throws|throws
name|Throwable
block|{
name|FsDatasetSpi
argument_list|<
name|?
argument_list|>
name|inst
init|=
operator|new
name|ExternalDatasetImpl
argument_list|()
decl_stmt|;
block|}
comment|/**    * Tests instantiating a Replica subclass.    */
annotation|@
name|Test
DECL|method|testIntantiateExternalReplica ()
specifier|public
name|void
name|testIntantiateExternalReplica
parameter_list|()
throws|throws
name|Throwable
block|{
name|Replica
name|inst
init|=
operator|new
name|ExternalReplica
argument_list|()
decl_stmt|;
block|}
comment|/**    * Tests instantiating a ReplicaInPipelineInterface subclass.    */
annotation|@
name|Test
DECL|method|testInstantiateReplicaInPipeline ()
specifier|public
name|void
name|testInstantiateReplicaInPipeline
parameter_list|()
throws|throws
name|Throwable
block|{
name|ReplicaInPipeline
name|inst
init|=
operator|new
name|ExternalReplicaInPipeline
argument_list|()
decl_stmt|;
block|}
comment|/**    * Tests instantiating an FsVolumeSpi subclass.    */
annotation|@
name|Test
DECL|method|testInstantiateVolumeImpl ()
specifier|public
name|void
name|testInstantiateVolumeImpl
parameter_list|()
throws|throws
name|Throwable
block|{
name|FsVolumeSpi
name|inst
init|=
operator|new
name|ExternalVolumeImpl
argument_list|()
decl_stmt|;
block|}
block|}
end_class

end_unit

