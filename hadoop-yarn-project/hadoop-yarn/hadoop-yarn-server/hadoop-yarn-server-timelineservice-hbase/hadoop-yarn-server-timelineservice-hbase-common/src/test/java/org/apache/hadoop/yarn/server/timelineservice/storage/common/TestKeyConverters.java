begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
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
name|timelineservice
operator|.
name|storage
operator|.
name|common
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
name|assertNull
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hbase
operator|.
name|util
operator|.
name|Bytes
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
name|ApplicationId
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
comment|/**  * Unit tests for key converters for various tables' row keys.  *  */
end_comment

begin_class
DECL|class|TestKeyConverters
specifier|public
class|class
name|TestKeyConverters
block|{
annotation|@
name|Test
DECL|method|testAppIdKeyConverter ()
specifier|public
name|void
name|testAppIdKeyConverter
parameter_list|()
block|{
name|AppIdKeyConverter
name|appIdKeyConverter
init|=
operator|new
name|AppIdKeyConverter
argument_list|()
decl_stmt|;
name|long
name|currentTs
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|ApplicationId
name|appId1
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|currentTs
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|ApplicationId
name|appId2
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|currentTs
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|ApplicationId
name|appId3
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|currentTs
operator|+
literal|300
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|appIdStr1
init|=
name|appId1
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|appIdStr2
init|=
name|appId2
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|appIdStr3
init|=
name|appId3
operator|.
name|toString
argument_list|()
decl_stmt|;
name|byte
index|[]
name|appIdBytes1
init|=
name|appIdKeyConverter
operator|.
name|encode
argument_list|(
name|appIdStr1
argument_list|)
decl_stmt|;
name|byte
index|[]
name|appIdBytes2
init|=
name|appIdKeyConverter
operator|.
name|encode
argument_list|(
name|appIdStr2
argument_list|)
decl_stmt|;
name|byte
index|[]
name|appIdBytes3
init|=
name|appIdKeyConverter
operator|.
name|encode
argument_list|(
name|appIdStr3
argument_list|)
decl_stmt|;
comment|// App ids' should be encoded in a manner wherein descending order
comment|// is maintained.
name|assertTrue
argument_list|(
literal|"Ordering of app ids' is incorrect"
argument_list|,
name|Bytes
operator|.
name|compareTo
argument_list|(
name|appIdBytes1
argument_list|,
name|appIdBytes2
argument_list|)
operator|>
literal|0
operator|&&
name|Bytes
operator|.
name|compareTo
argument_list|(
name|appIdBytes1
argument_list|,
name|appIdBytes3
argument_list|)
operator|>
literal|0
operator|&&
name|Bytes
operator|.
name|compareTo
argument_list|(
name|appIdBytes2
argument_list|,
name|appIdBytes3
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
name|String
name|decodedAppId1
init|=
name|appIdKeyConverter
operator|.
name|decode
argument_list|(
name|appIdBytes1
argument_list|)
decl_stmt|;
name|String
name|decodedAppId2
init|=
name|appIdKeyConverter
operator|.
name|decode
argument_list|(
name|appIdBytes2
argument_list|)
decl_stmt|;
name|String
name|decodedAppId3
init|=
name|appIdKeyConverter
operator|.
name|decode
argument_list|(
name|appIdBytes3
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Decoded app id is not same as the app id encoded"
argument_list|,
name|appIdStr1
operator|.
name|equals
argument_list|(
name|decodedAppId1
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Decoded app id is not same as the app id encoded"
argument_list|,
name|appIdStr2
operator|.
name|equals
argument_list|(
name|decodedAppId2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Decoded app id is not same as the app id encoded"
argument_list|,
name|appIdStr3
operator|.
name|equals
argument_list|(
name|decodedAppId3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEventColumnNameConverter ()
specifier|public
name|void
name|testEventColumnNameConverter
parameter_list|()
block|{
name|String
name|eventId
init|=
literal|"=foo_=eve=nt="
decl_stmt|;
name|byte
index|[]
name|valSepBytes
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Separator
operator|.
name|VALUES
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|maxByteArr
init|=
name|Bytes
operator|.
name|createMaxByteArray
argument_list|(
name|Bytes
operator|.
name|SIZEOF_LONG
operator|-
name|valSepBytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|byte
index|[]
name|ts
init|=
name|Bytes
operator|.
name|add
argument_list|(
name|valSepBytes
argument_list|,
name|maxByteArr
argument_list|)
decl_stmt|;
name|Long
name|eventTs
init|=
name|Bytes
operator|.
name|toLong
argument_list|(
name|ts
argument_list|)
decl_stmt|;
name|byte
index|[]
name|byteEventColName
init|=
operator|new
name|EventColumnName
argument_list|(
name|eventId
argument_list|,
name|eventTs
argument_list|,
literal|null
argument_list|)
operator|.
name|getColumnQualifier
argument_list|()
decl_stmt|;
name|KeyConverter
argument_list|<
name|EventColumnName
argument_list|>
name|eventColumnNameConverter
init|=
operator|new
name|EventColumnNameConverter
argument_list|()
decl_stmt|;
name|EventColumnName
name|eventColName
init|=
name|eventColumnNameConverter
operator|.
name|decode
argument_list|(
name|byteEventColName
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|eventId
argument_list|,
name|eventColName
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|eventTs
argument_list|,
name|eventColName
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|eventColName
operator|.
name|getInfoKey
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|infoKey
init|=
literal|"f=oo_event_in=fo=_key"
decl_stmt|;
name|byteEventColName
operator|=
operator|new
name|EventColumnName
argument_list|(
name|eventId
argument_list|,
name|eventTs
argument_list|,
name|infoKey
argument_list|)
operator|.
name|getColumnQualifier
argument_list|()
expr_stmt|;
name|eventColName
operator|=
name|eventColumnNameConverter
operator|.
name|decode
argument_list|(
name|byteEventColName
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|eventId
argument_list|,
name|eventColName
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|eventTs
argument_list|,
name|eventColName
operator|.
name|getTimestamp
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|infoKey
argument_list|,
name|eventColName
operator|.
name|getInfoKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLongKeyConverter ()
specifier|public
name|void
name|testLongKeyConverter
parameter_list|()
block|{
name|LongKeyConverter
name|longKeyConverter
init|=
operator|new
name|LongKeyConverter
argument_list|()
decl_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
name|Long
operator|.
name|MIN_VALUE
argument_list|)
expr_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
operator|-
literal|1234567890L
argument_list|)
expr_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
operator|-
literal|128L
argument_list|)
expr_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
operator|-
literal|127L
argument_list|)
expr_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
operator|-
literal|1L
argument_list|)
expr_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
literal|127L
argument_list|)
expr_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
literal|128L
argument_list|)
expr_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
literal|1234567890L
argument_list|)
expr_stmt|;
name|confirmLongKeyConverter
argument_list|(
name|longKeyConverter
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
DECL|method|confirmLongKeyConverter (LongKeyConverter longKeyConverter, Long testValue)
specifier|private
name|void
name|confirmLongKeyConverter
parameter_list|(
name|LongKeyConverter
name|longKeyConverter
parameter_list|,
name|Long
name|testValue
parameter_list|)
block|{
name|Long
name|decoded
init|=
name|longKeyConverter
operator|.
name|decode
argument_list|(
name|longKeyConverter
operator|.
name|encode
argument_list|(
name|testValue
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testValue
argument_list|,
name|decoded
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testStringKeyConverter ()
specifier|public
name|void
name|testStringKeyConverter
parameter_list|()
block|{
name|StringKeyConverter
name|stringKeyConverter
init|=
operator|new
name|StringKeyConverter
argument_list|()
decl_stmt|;
name|String
name|phrase
init|=
literal|"QuackAttack now!"
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|phrase
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|sub
init|=
name|phrase
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|phrase
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|confirmStrignKeyConverter
argument_list|(
name|stringKeyConverter
argument_list|,
name|sub
argument_list|)
expr_stmt|;
name|confirmStrignKeyConverter
argument_list|(
name|stringKeyConverter
argument_list|,
name|sub
operator|+
name|sub
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|confirmStrignKeyConverter (StringKeyConverter stringKeyConverter, String testValue)
specifier|private
name|void
name|confirmStrignKeyConverter
parameter_list|(
name|StringKeyConverter
name|stringKeyConverter
parameter_list|,
name|String
name|testValue
parameter_list|)
block|{
name|String
name|decoded
init|=
name|stringKeyConverter
operator|.
name|decode
argument_list|(
name|stringKeyConverter
operator|.
name|encode
argument_list|(
name|testValue
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|testValue
argument_list|,
name|decoded
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

