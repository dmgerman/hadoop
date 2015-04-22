begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.erasurecode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|erasurecode
package|;
end_package

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
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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

begin_class
DECL|class|TestECSchema
specifier|public
class|class
name|TestECSchema
block|{
annotation|@
name|Test
DECL|method|testGoodSchema ()
specifier|public
name|void
name|testGoodSchema
parameter_list|()
block|{
name|String
name|schemaName
init|=
literal|"goodSchema"
decl_stmt|;
name|int
name|numDataUnits
init|=
literal|6
decl_stmt|;
name|int
name|numParityUnits
init|=
literal|3
decl_stmt|;
name|int
name|chunkSize
init|=
literal|64
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
name|String
name|codec
init|=
literal|"rs"
decl_stmt|;
name|String
name|extraOption
init|=
literal|"extraOption"
decl_stmt|;
name|String
name|extraOptionValue
init|=
literal|"extraOptionValue"
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|options
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|options
operator|.
name|put
argument_list|(
name|ECSchema
operator|.
name|NUM_DATA_UNITS_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|numDataUnits
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|ECSchema
operator|.
name|NUM_PARITY_UNITS_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|numParityUnits
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|ECSchema
operator|.
name|CODEC_NAME_KEY
argument_list|,
name|codec
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|ECSchema
operator|.
name|CHUNK_SIZE_KEY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|chunkSize
argument_list|)
argument_list|)
expr_stmt|;
name|options
operator|.
name|put
argument_list|(
name|extraOption
argument_list|,
name|extraOptionValue
argument_list|)
expr_stmt|;
name|ECSchema
name|schema
init|=
operator|new
name|ECSchema
argument_list|(
name|schemaName
argument_list|,
name|options
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|schema
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|schemaName
argument_list|,
name|schema
operator|.
name|getSchemaName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numDataUnits
argument_list|,
name|schema
operator|.
name|getNumDataUnits
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|numParityUnits
argument_list|,
name|schema
operator|.
name|getNumParityUnits
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|chunkSize
argument_list|,
name|schema
operator|.
name|getChunkSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|codec
argument_list|,
name|schema
operator|.
name|getCodecName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|extraOptionValue
argument_list|,
name|schema
operator|.
name|getExtraOptions
argument_list|()
operator|.
name|get
argument_list|(
name|extraOption
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

