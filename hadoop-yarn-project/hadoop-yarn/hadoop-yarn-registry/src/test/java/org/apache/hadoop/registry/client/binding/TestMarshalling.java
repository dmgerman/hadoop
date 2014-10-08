begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.registry.client.binding
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|registry
operator|.
name|client
operator|.
name|binding
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
name|registry
operator|.
name|RegistryTestHelper
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
name|registry
operator|.
name|client
operator|.
name|exceptions
operator|.
name|NoRecordException
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecordHeader
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|yarn
operator|.
name|PersistencePolicies
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TestName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|EOFException
import|;
end_import

begin_comment
comment|/**  * Test record marshalling  */
end_comment

begin_class
DECL|class|TestMarshalling
specifier|public
class|class
name|TestMarshalling
extends|extends
name|RegistryTestHelper
block|{
specifier|private
specifier|static
specifier|final
name|Logger
DECL|field|LOG
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestMarshalling
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|testTimeout
specifier|public
specifier|final
name|Timeout
name|testTimeout
init|=
operator|new
name|Timeout
argument_list|(
literal|10000
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|methodName
specifier|public
name|TestName
name|methodName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
DECL|field|marshal
specifier|private
specifier|static
name|RegistryUtils
operator|.
name|ServiceRecordMarshal
name|marshal
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setupClass ()
specifier|public
specifier|static
name|void
name|setupClass
parameter_list|()
block|{
name|marshal
operator|=
operator|new
name|RegistryUtils
operator|.
name|ServiceRecordMarshal
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRoundTrip ()
specifier|public
name|void
name|testRoundTrip
parameter_list|()
throws|throws
name|Throwable
block|{
name|String
name|persistence
init|=
name|PersistencePolicies
operator|.
name|PERMANENT
decl_stmt|;
name|ServiceRecord
name|record
init|=
name|createRecord
argument_list|(
name|persistence
argument_list|)
decl_stmt|;
name|record
operator|.
name|set
argument_list|(
literal|"customkey"
argument_list|,
literal|"customvalue"
argument_list|)
expr_stmt|;
name|record
operator|.
name|set
argument_list|(
literal|"customkey2"
argument_list|,
literal|"customvalue2"
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|marshal
operator|.
name|toJson
argument_list|(
name|record
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|marshal
operator|.
name|toBytes
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|ServiceRecord
name|r2
init|=
name|marshal
operator|.
name|fromBytes
argument_list|(
literal|""
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|record
argument_list|,
name|r2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRoundTripHeaders ()
specifier|public
name|void
name|testRoundTripHeaders
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceRecord
name|record
init|=
name|createRecord
argument_list|(
name|PersistencePolicies
operator|.
name|CONTAINER
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|marshal
operator|.
name|toByteswithHeader
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|ServiceRecord
name|r2
init|=
name|marshal
operator|.
name|fromBytesWithHeader
argument_list|(
literal|""
argument_list|,
name|bytes
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|record
argument_list|,
name|r2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NoRecordException
operator|.
name|class
argument_list|)
DECL|method|testRoundTripBadHeaders ()
specifier|public
name|void
name|testRoundTripBadHeaders
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceRecord
name|record
init|=
name|createRecord
argument_list|(
name|PersistencePolicies
operator|.
name|APPLICATION
argument_list|)
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
name|marshal
operator|.
name|toByteswithHeader
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|bytes
index|[
literal|1
index|]
operator|=
literal|0x01
expr_stmt|;
name|marshal
operator|.
name|fromBytesWithHeader
argument_list|(
literal|"src"
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NoRecordException
operator|.
name|class
argument_list|)
DECL|method|testUnmarshallHeaderTooShort ()
specifier|public
name|void
name|testUnmarshallHeaderTooShort
parameter_list|()
throws|throws
name|Throwable
block|{
name|marshal
operator|.
name|fromBytesWithHeader
argument_list|(
literal|"src"
argument_list|,
operator|new
name|byte
index|[]
block|{
literal|'a'
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|EOFException
operator|.
name|class
argument_list|)
DECL|method|testUnmarshallNoBody ()
specifier|public
name|void
name|testUnmarshallNoBody
parameter_list|()
throws|throws
name|Throwable
block|{
name|byte
index|[]
name|bytes
init|=
name|ServiceRecordHeader
operator|.
name|getData
argument_list|()
decl_stmt|;
name|marshal
operator|.
name|fromBytesWithHeader
argument_list|(
literal|"src"
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testUnknownFieldsRoundTrip ()
specifier|public
name|void
name|testUnknownFieldsRoundTrip
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceRecord
name|record
init|=
name|createRecord
argument_list|(
name|PersistencePolicies
operator|.
name|APPLICATION_ATTEMPT
argument_list|)
decl_stmt|;
name|record
operator|.
name|set
argument_list|(
literal|"key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|record
operator|.
name|set
argument_list|(
literal|"intval"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|record
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|record
operator|.
name|get
argument_list|(
literal|"intval"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|record
operator|.
name|get
argument_list|(
literal|"null"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"defval"
argument_list|,
name|record
operator|.
name|get
argument_list|(
literal|"null"
argument_list|,
literal|"defval"
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|marshal
operator|.
name|toByteswithHeader
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|ServiceRecord
name|r2
init|=
name|marshal
operator|.
name|fromBytesWithHeader
argument_list|(
literal|""
argument_list|,
name|bytes
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|r2
operator|.
name|get
argument_list|(
literal|"key"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|r2
operator|.
name|get
argument_list|(
literal|"intval"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFieldPropagationInCopy ()
specifier|public
name|void
name|testFieldPropagationInCopy
parameter_list|()
throws|throws
name|Throwable
block|{
name|ServiceRecord
name|record
init|=
name|createRecord
argument_list|(
name|PersistencePolicies
operator|.
name|APPLICATION_ATTEMPT
argument_list|)
decl_stmt|;
name|record
operator|.
name|set
argument_list|(
literal|"key"
argument_list|,
literal|"value"
argument_list|)
expr_stmt|;
name|record
operator|.
name|set
argument_list|(
literal|"intval"
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|ServiceRecord
name|that
init|=
operator|new
name|ServiceRecord
argument_list|(
name|record
argument_list|)
decl_stmt|;
name|assertMatches
argument_list|(
name|record
argument_list|,
name|that
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

