begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.metrics2.lib
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
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
name|*
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
name|metrics2
operator|.
name|MetricsInfo
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
name|metrics2
operator|.
name|MetricsTag
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|metrics2
operator|.
name|lib
operator|.
name|Interns
operator|.
name|*
import|;
end_import

begin_class
DECL|class|TestInterns
specifier|public
class|class
name|TestInterns
block|{
DECL|method|testInfo ()
annotation|@
name|Test
specifier|public
name|void
name|testInfo
parameter_list|()
block|{
name|MetricsInfo
name|info
init|=
name|info
argument_list|(
literal|"m"
argument_list|,
literal|"m desc"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"same info"
argument_list|,
name|info
argument_list|,
name|info
argument_list|(
literal|"m"
argument_list|,
literal|"m desc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTag ()
annotation|@
name|Test
specifier|public
name|void
name|testTag
parameter_list|()
block|{
name|MetricsTag
name|tag
init|=
name|tag
argument_list|(
literal|"t"
argument_list|,
literal|"t desc"
argument_list|,
literal|"t value"
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
literal|"same tag"
argument_list|,
name|tag
argument_list|,
name|tag
argument_list|(
literal|"t"
argument_list|,
literal|"t desc"
argument_list|,
literal|"t value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testInfoOverflow ()
annotation|@
name|Test
specifier|public
name|void
name|testInfoOverflow
parameter_list|()
block|{
name|MetricsInfo
name|i0
init|=
name|info
argument_list|(
literal|"m0"
argument_list|,
literal|"m desc"
argument_list|)
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
name|MAX_INFO_NAMES
operator|+
literal|1
condition|;
operator|++
name|i
control|)
block|{
name|info
argument_list|(
literal|"m"
operator|+
name|i
argument_list|,
literal|"m desc"
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|MAX_INFO_NAMES
condition|)
block|{
name|assertSame
argument_list|(
literal|"m0 is still there"
argument_list|,
name|i0
argument_list|,
name|info
argument_list|(
literal|"m0"
argument_list|,
literal|"m desc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNotSame
argument_list|(
literal|"m0 is gone"
argument_list|,
name|i0
argument_list|,
name|info
argument_list|(
literal|"m0"
argument_list|,
literal|"m desc"
argument_list|)
argument_list|)
expr_stmt|;
name|MetricsInfo
name|i1
init|=
name|info
argument_list|(
literal|"m1"
argument_list|,
literal|"m desc"
argument_list|)
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
name|MAX_INFO_DESCS
condition|;
operator|++
name|i
control|)
block|{
name|info
argument_list|(
literal|"m1"
argument_list|,
literal|"m desc"
operator|+
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|MAX_INFO_DESCS
operator|-
literal|1
condition|)
block|{
name|assertSame
argument_list|(
literal|"i1 is still there"
argument_list|,
name|i1
argument_list|,
name|info
argument_list|(
literal|"m1"
argument_list|,
literal|"m desc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNotSame
argument_list|(
literal|"i1 is gone"
argument_list|,
name|i1
argument_list|,
name|info
argument_list|(
literal|"m1"
argument_list|,
literal|"m desc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testTagOverflow ()
annotation|@
name|Test
specifier|public
name|void
name|testTagOverflow
parameter_list|()
block|{
name|MetricsTag
name|t0
init|=
name|tag
argument_list|(
literal|"t0"
argument_list|,
literal|"t desc"
argument_list|,
literal|"t value"
argument_list|)
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
name|MAX_TAG_NAMES
operator|+
literal|1
condition|;
operator|++
name|i
control|)
block|{
name|tag
argument_list|(
literal|"t"
operator|+
name|i
argument_list|,
literal|"t desc"
argument_list|,
literal|"t value"
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|MAX_TAG_NAMES
condition|)
block|{
name|assertSame
argument_list|(
literal|"t0 still there"
argument_list|,
name|t0
argument_list|,
name|tag
argument_list|(
literal|"t0"
argument_list|,
literal|"t desc"
argument_list|,
literal|"t value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNotSame
argument_list|(
literal|"t0 is gone"
argument_list|,
name|t0
argument_list|,
name|tag
argument_list|(
literal|"t0"
argument_list|,
literal|"t desc"
argument_list|,
literal|"t value"
argument_list|)
argument_list|)
expr_stmt|;
name|MetricsTag
name|t1
init|=
name|tag
argument_list|(
literal|"t1"
argument_list|,
literal|"t desc"
argument_list|,
literal|"t value"
argument_list|)
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
name|MAX_TAG_VALUES
condition|;
operator|++
name|i
control|)
block|{
name|tag
argument_list|(
literal|"t1"
argument_list|,
literal|"t desc"
argument_list|,
literal|"t value"
operator|+
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|MAX_TAG_VALUES
operator|-
literal|1
condition|)
block|{
name|assertSame
argument_list|(
literal|"t1 is still there"
argument_list|,
name|t1
argument_list|,
name|tag
argument_list|(
literal|"t1"
argument_list|,
literal|"t desc"
argument_list|,
literal|"t value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|assertNotSame
argument_list|(
literal|"t1 is gone"
argument_list|,
name|t1
argument_list|,
name|tag
argument_list|(
literal|"t1"
argument_list|,
literal|"t desc"
argument_list|,
literal|"t value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

