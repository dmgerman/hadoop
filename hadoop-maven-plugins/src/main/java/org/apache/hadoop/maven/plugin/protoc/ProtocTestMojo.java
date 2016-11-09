begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.maven.plugin.protoc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|maven
operator|.
name|plugin
operator|.
name|protoc
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|model
operator|.
name|FileSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|AbstractMojo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugin
operator|.
name|MojoExecutionException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|annotations
operator|.
name|LifecyclePhase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|annotations
operator|.
name|Mojo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|plugins
operator|.
name|annotations
operator|.
name|Parameter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|maven
operator|.
name|project
operator|.
name|MavenProject
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  * Mojo to generate java test classes from .proto files using protoc.  * See package info for examples of use in a maven pom.  */
end_comment

begin_class
annotation|@
name|Mojo
argument_list|(
name|name
operator|=
literal|"test-protoc"
argument_list|,
name|defaultPhase
operator|=
name|LifecyclePhase
operator|.
name|GENERATE_TEST_SOURCES
argument_list|)
DECL|class|ProtocTestMojo
specifier|public
class|class
name|ProtocTestMojo
extends|extends
name|AbstractMojo
block|{
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"${project}"
argument_list|,
name|readonly
operator|=
literal|true
argument_list|)
DECL|field|project
specifier|private
name|MavenProject
name|project
decl_stmt|;
annotation|@
name|Parameter
DECL|field|imports
specifier|private
name|File
index|[]
name|imports
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"${project.build.directory}/generated-test-sources/java"
argument_list|)
DECL|field|output
specifier|private
name|File
name|output
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|required
operator|=
literal|true
argument_list|)
DECL|field|source
specifier|private
name|FileSet
name|source
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"protoc"
argument_list|)
DECL|field|protocCommand
specifier|private
name|String
name|protocCommand
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|required
operator|=
literal|true
argument_list|)
DECL|field|protocVersion
specifier|private
name|String
name|protocVersion
decl_stmt|;
annotation|@
name|Parameter
argument_list|(
name|defaultValue
operator|=
literal|"${project.build.directory}/hadoop-maven-plugins-protoc-checksums.json"
argument_list|)
DECL|field|checksumPath
specifier|private
name|String
name|checksumPath
decl_stmt|;
DECL|method|execute ()
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|MojoExecutionException
block|{
specifier|final
name|ProtocRunner
name|protoc
init|=
operator|new
name|ProtocRunner
argument_list|(
name|project
argument_list|,
name|imports
argument_list|,
name|output
argument_list|,
name|source
argument_list|,
name|protocCommand
argument_list|,
name|protocVersion
argument_list|,
name|checksumPath
argument_list|,
name|this
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|protoc
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

