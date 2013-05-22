begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.api.protocolrecords.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
package|;
end_package

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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertTrue
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|io
operator|.
name|DataInputBuffer
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
name|io
operator|.
name|DataOutputBuffer
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
name|api
operator|.
name|records
operator|.
name|LocalResource
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
name|api
operator|.
name|records
operator|.
name|LocalResourceVisibility
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
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|LocalResourcePBImpl
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|proto
operator|.
name|YarnServerNodemanagerServiceProtos
operator|.
name|LocalResourceStatusProto
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
name|proto
operator|.
name|YarnServerNodemanagerServiceProtos
operator|.
name|LocalizerHeartbeatResponseProto
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
name|proto
operator|.
name|YarnServerNodemanagerServiceProtos
operator|.
name|LocalizerStatusProto
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
name|server
operator|.
name|nodemanager
operator|.
name|api
operator|.
name|ResourceLocalizationSpec
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
name|server
operator|.
name|nodemanager
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|LocalResourceStatus
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
name|server
operator|.
name|nodemanager
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|LocalizerAction
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
name|server
operator|.
name|nodemanager
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|LocalizerHeartbeatResponse
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
name|server
operator|.
name|nodemanager
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|LocalizerStatus
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
name|server
operator|.
name|nodemanager
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|ResourceStatusType
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
name|server
operator|.
name|utils
operator|.
name|YarnServerBuilderUtils
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
name|util
operator|.
name|ConverterUtils
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

begin_class
DECL|class|TestPBRecordImpl
specifier|public
class|class
name|TestPBRecordImpl
block|{
DECL|field|recordFactory
specifier|static
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|createPBRecordFactory
argument_list|()
decl_stmt|;
DECL|method|createPBRecordFactory ()
specifier|static
name|RecordFactory
name|createPBRecordFactory
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
return|return
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
name|conf
argument_list|)
return|;
block|}
DECL|method|createResource ()
specifier|static
name|LocalResource
name|createResource
parameter_list|()
block|{
name|LocalResource
name|ret
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|LocalResource
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ret
operator|instanceof
name|LocalResourcePBImpl
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setResource
argument_list|(
name|ConverterUtils
operator|.
name|getYarnUrlFromPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"hdfs://y.ak:8020/foo/bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setSize
argument_list|(
literal|4344L
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setTimestamp
argument_list|(
literal|3141592653589793L
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setVisibility
argument_list|(
name|LocalResourceVisibility
operator|.
name|PUBLIC
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|createLocalResourceStatus ()
specifier|static
name|LocalResourceStatus
name|createLocalResourceStatus
parameter_list|()
block|{
name|LocalResourceStatus
name|ret
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|LocalResourceStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ret
operator|instanceof
name|LocalResourceStatusPBImpl
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setResource
argument_list|(
name|createResource
argument_list|()
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setLocalPath
argument_list|(
name|ConverterUtils
operator|.
name|getYarnUrlFromPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"file:///local/foo/bar"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setStatus
argument_list|(
name|ResourceStatusType
operator|.
name|FETCH_SUCCESS
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setLocalSize
argument_list|(
literal|4443L
argument_list|)
expr_stmt|;
name|Exception
name|e
init|=
operator|new
name|Exception
argument_list|(
literal|"Dingos."
argument_list|)
decl_stmt|;
name|e
operator|.
name|setStackTrace
argument_list|(
operator|new
name|StackTraceElement
index|[]
block|{
operator|new
name|StackTraceElement
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|,
literal|"baz"
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|StackTraceElement
argument_list|(
literal|"sbb"
argument_list|,
literal|"one"
argument_list|,
literal|"onm"
argument_list|,
literal|10
argument_list|)
block|}
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setException
argument_list|(
name|YarnServerBuilderUtils
operator|.
name|newSerializedException
argument_list|(
name|e
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|createLocalizerStatus ()
specifier|static
name|LocalizerStatus
name|createLocalizerStatus
parameter_list|()
block|{
name|LocalizerStatus
name|ret
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|LocalizerStatus
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ret
operator|instanceof
name|LocalizerStatusPBImpl
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setLocalizerId
argument_list|(
literal|"localizer0"
argument_list|)
expr_stmt|;
name|ret
operator|.
name|addResourceStatus
argument_list|(
name|createLocalResourceStatus
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
DECL|method|createLocalizerHeartbeatResponse ()
specifier|static
name|LocalizerHeartbeatResponse
name|createLocalizerHeartbeatResponse
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|LocalizerHeartbeatResponse
name|ret
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|LocalizerHeartbeatResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|ret
operator|instanceof
name|LocalizerHeartbeatResponsePBImpl
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setLocalizerAction
argument_list|(
name|LocalizerAction
operator|.
name|LIVE
argument_list|)
expr_stmt|;
name|LocalResource
name|rsrc
init|=
name|createResource
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|ResourceLocalizationSpec
argument_list|>
name|rsrcs
init|=
operator|new
name|ArrayList
argument_list|<
name|ResourceLocalizationSpec
argument_list|>
argument_list|()
decl_stmt|;
name|ResourceLocalizationSpec
name|resource
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|ResourceLocalizationSpec
operator|.
name|class
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setResource
argument_list|(
name|rsrc
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setDestinationDirectory
argument_list|(
name|ConverterUtils
operator|.
name|getYarnUrlFromPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/tmp"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rsrcs
operator|.
name|add
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|ret
operator|.
name|setResourceSpecs
argument_list|(
name|rsrcs
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|resource
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testLocalResourceStatusSerDe ()
specifier|public
name|void
name|testLocalResourceStatusSerDe
parameter_list|()
throws|throws
name|Exception
block|{
name|LocalResourceStatus
name|rsrcS
init|=
name|createLocalResourceStatus
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|rsrcS
operator|instanceof
name|LocalResourceStatusPBImpl
argument_list|)
expr_stmt|;
name|LocalResourceStatusPBImpl
name|rsrcPb
init|=
operator|(
name|LocalResourceStatusPBImpl
operator|)
name|rsrcS
decl_stmt|;
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|rsrcPb
operator|.
name|getProto
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|LocalResourceStatusProto
name|rsrcPbD
init|=
name|LocalResourceStatusProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|rsrcPbD
argument_list|)
expr_stmt|;
name|LocalResourceStatus
name|rsrcD
init|=
operator|new
name|LocalResourceStatusPBImpl
argument_list|(
name|rsrcPbD
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|rsrcS
argument_list|,
name|rsrcD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|()
argument_list|,
name|rsrcS
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|()
argument_list|,
name|rsrcD
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testLocalizerStatusSerDe ()
specifier|public
name|void
name|testLocalizerStatusSerDe
parameter_list|()
throws|throws
name|Exception
block|{
name|LocalizerStatus
name|rsrcS
init|=
name|createLocalizerStatus
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|rsrcS
operator|instanceof
name|LocalizerStatusPBImpl
argument_list|)
expr_stmt|;
name|LocalizerStatusPBImpl
name|rsrcPb
init|=
operator|(
name|LocalizerStatusPBImpl
operator|)
name|rsrcS
decl_stmt|;
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|rsrcPb
operator|.
name|getProto
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|LocalizerStatusProto
name|rsrcPbD
init|=
name|LocalizerStatusProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|rsrcPbD
argument_list|)
expr_stmt|;
name|LocalizerStatus
name|rsrcD
init|=
operator|new
name|LocalizerStatusPBImpl
argument_list|(
name|rsrcPbD
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|rsrcS
argument_list|,
name|rsrcD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localizer0"
argument_list|,
name|rsrcS
operator|.
name|getLocalizerId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"localizer0"
argument_list|,
name|rsrcD
operator|.
name|getLocalizerId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createLocalResourceStatus
argument_list|()
argument_list|,
name|rsrcS
operator|.
name|getResourceStatus
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createLocalResourceStatus
argument_list|()
argument_list|,
name|rsrcD
operator|.
name|getResourceStatus
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testLocalizerHeartbeatResponseSerDe ()
specifier|public
name|void
name|testLocalizerHeartbeatResponseSerDe
parameter_list|()
throws|throws
name|Exception
block|{
name|LocalizerHeartbeatResponse
name|rsrcS
init|=
name|createLocalizerHeartbeatResponse
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|rsrcS
operator|instanceof
name|LocalizerHeartbeatResponsePBImpl
argument_list|)
expr_stmt|;
name|LocalizerHeartbeatResponsePBImpl
name|rsrcPb
init|=
operator|(
name|LocalizerHeartbeatResponsePBImpl
operator|)
name|rsrcS
decl_stmt|;
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|rsrcPb
operator|.
name|getProto
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|DataInputBuffer
name|in
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
name|in
operator|.
name|reset
argument_list|(
name|out
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|out
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|LocalizerHeartbeatResponseProto
name|rsrcPbD
init|=
name|LocalizerHeartbeatResponseProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|rsrcPbD
argument_list|)
expr_stmt|;
name|LocalizerHeartbeatResponse
name|rsrcD
init|=
operator|new
name|LocalizerHeartbeatResponsePBImpl
argument_list|(
name|rsrcPbD
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|rsrcS
argument_list|,
name|rsrcD
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|()
argument_list|,
name|rsrcS
operator|.
name|getResourceSpecs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|createResource
argument_list|()
argument_list|,
name|rsrcD
operator|.
name|getResourceSpecs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getResource
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

