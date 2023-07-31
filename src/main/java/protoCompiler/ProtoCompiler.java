package ProtoCompiler.src.main.java.protocompiler;

import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class ProtoCompiler {

    private static String appDir;
    private static String packageDir;
    private static String outputDir;
    private static String className;

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Usage: ProtobufCompiler <appDir> <packageDir> <outputDir>");
            System.exit(1);
        }

        appDir = args[0];
        packageDir = args[1];
        outputDir = args[2];

        System.out.println("Scanning classes from directory: " + appDir + packageDir);
        List<Class<?>> classes = scanClassesFromDirectory(appDir + packageDir);
        System.out.println("Found " + classes.size() + " classes.");

        List<ProtoService> request = getCodeGeneratorRequest(appDir, packageDir);

        for (ProtoService proto : request) {
            className = proto.getServiceName();
            CodeGeneratorResponse response = createCodeGeneratorResponse(proto);

            saveProtobufFiles(response, outputDir);
        }

        System.out.println("Completed successfully.");
    }

    private static List<ProtoService> getCodeGeneratorRequest(String appDir, String packageDir) {
        List<ProtoService> protoServiceList = new ArrayList<>();

        try {
            // Verilen dizindeki Java sınıflarını oku
            List<Class<?>> classes = scanClassesFromDirectory(appDir + packageDir);

            for (Class<?> clazz : classes) {
                // Sınıfın Protobuf mesaj ve hizmet tanımlarını oluştur
                ProtoService protoService = getProtoService(clazz);
                protoServiceList.add(protoService);
            }
        } catch (IOException e) {
            System.err.println("Dizin okunurken bir hata oluştu: " + e.getMessage());
        }

        return protoServiceList;
    }

    private static List<Class<?>> scanClassesFromDirectory(String directory) throws IOException {
        List<Class<?>> classes = new ArrayList<>();

        // Dizin altındaki Java sınıflarını bul ve listeye ekle
        Files.walk(Path.of(directory))
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    System.out.println("Found file: " + file);
                    String packageName = getClassName(file);
                    if (packageName != null) {
                        try {
                            java.io.File f = new java.io.File(appDir);
                            URL[] cp = {f.toURI().toURL()};
                            URLClassLoader urlcl = new URLClassLoader(cp);
                            Class clazz = urlcl.loadClass(packageName);
                            classes.add(clazz);
                        } catch (ClassNotFoundException e) {
                            System.err.println("Sınıf bulunamadı: " + packageName);
                        } catch (MalformedURLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        return classes;
    }

    private static String getClassName(Path filePath) {
        if (filePath.toString().endsWith(".class")) {
            java.io.File file = new java.io.File(filePath.toString());
            String fileName = file.getName();

            String className = fileName.replace(".class", "");

            String packagePath = filePath.toString().substring(0, filePath.toString().lastIndexOf("/"));
            String[] folders = packagePath.split("/");
            String packageName = "";

            Integer index = 0;
            Integer comIndex = Arrays.asList(folders).indexOf("com");
            for (String folder : folders) {
                if (index >= comIndex) {
                    packageName += folder + ".";
                }

                index++;
            }

            packageName += className;

            System.out.println("Package Name: " + packageName);

            return packageName;
        }
        return null;
    }

    private static ProtoService getProtoService(Class<?> clazz) {
        ProtoService protoService = null;
        try {
            List<ProtoMessage> requestMessages = getRequestMessages(clazz);
            List<ProtoMessage> responseMessages = getResponseMessages(clazz);

            protoService = createProtoServiceFromMessages(requestMessages, responseMessages, clazz);
        } catch (Exception e) {
            System.err.println("Protobuf hizmeti oluşturulurken bir hata oluştu: " + e.getMessage());
        }
        return protoService;
    }

    private static List<ProtoMessage> getRequestMessages(Class<?> clazz) {
        List<ProtoMessage> requestMessages = new ArrayList<>();
        try {
            for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
                requestMessages.add(createProtoMessageFromClass(method, method.getName()));
            }

        } catch (Exception e) {
            System.err.println("Protobuf istek mesajları oluşturulurken bir hata oluştu: " + e.getMessage());
        }
        return requestMessages;
    }

    private static ProtoMessage createProtoMessageFromClass(java.lang.reflect.Method method, String methodName) {
        Parameter[] parameters = method.getParameters();

        ProtoMessage protoMessage = new ProtoMessage();
        protoMessage.setMessageName(method.getName());
        protoMessage.setParameters(parameters);
        protoMessage.setMethodName(methodName);
        protoMessage.setReturnName(method.getReturnType().getSimpleName());
        protoMessage.setReturnType(method.getReturnType());
        protoMessage.setGenericReturnType(method.getGenericReturnType());
        return protoMessage;
    }

    private static List<ProtoMessage> getResponseMessages(Class<?> clazz) {
        List<ProtoMessage> responseMessages = new ArrayList<>();
        try {
            for (java.lang.reflect.Method method : clazz.getDeclaredMethods()) {
                responseMessages.add(createProtoMessageFromClass(method, method.getName()));
            }
        } catch (Exception e) {
            System.err.println("Protobuf cevap mesajları oluşturulurken bir hata oluştu: " + e.getMessage());
        }
        return responseMessages;
    }

    private static ProtoService createProtoServiceFromMessages(List<ProtoMessage> requestMessages,
                                                               List<ProtoMessage> responseMessages,
                                                               Class<?> clazz) {
        ProtoService service = new ProtoService();
        service.setServiceName(clazz.getSimpleName());
        service.setRequestMessages(requestMessages);
        service.setResponseMessages(responseMessages);
        return service;
    }

    private static CodeGeneratorResponse createCodeGeneratorResponse(ProtoService request) {
        CodeGeneratorResponse.Builder responseBuilder = CodeGeneratorResponse.newBuilder();

        String protobufContent = generateProtobufContent(request);

        responseBuilder.addFile(
                File.newBuilder()
                        .setName(className + ".proto")
                        .setContent(protobufContent)
                        .build()
        );

        return responseBuilder.build();
    }

    private static String generateProtobufContent(ProtoService protoService) {
        StringBuilder contentBuilder = new StringBuilder();

        contentBuilder.append("syntax = \"proto3\";\n");
        contentBuilder.append("package ").append("packageName").append(";\n");
        contentBuilder.append("\n");
        contentBuilder.append("import \"google/protobuf/empty.proto\";\n");
        contentBuilder.append("\n");

        // Descriptors.Descriptor messageDescriptor = getMessageDescriptor(clazz);

        protoService.getResponseMessages().forEach(protoMessage -> {

            contentBuilder.append("message ").append(protoMessage.getMessageName().substring(0, 1).toUpperCase()
                    + protoMessage.getMessageName().substring(1)).append("Response {\n");

            List<ParameterDto> responseList = new ArrayList<>();
            ParameterDto respDto = new ParameterDto();
            respDto.setType(protoMessage.getReturnType());
            respDto.setName(protoMessage.getMessageName());
            respDto.setAnnotatedType(protoMessage.getReturnType().getAnnotatedSuperclass());
            respDto.setGenericReturnType(protoMessage.getGenericReturnType());

            responseList.add(respDto);

            contentBuilder.append(writeParameterType(responseList, contentBuilder));

            contentBuilder.append("message ").append(protoMessage.getMessageName().substring(0, 1).toUpperCase()
                    + protoMessage.getMessageName().substring(1)).append("Request {\n");

            List<ParameterDto> list = new ArrayList<>();
            for (Parameter parameter : protoMessage.getParameters()) {
                ParameterDto dto = new ParameterDto();
                dto.setAnnotatedType(parameter.getAnnotatedType());
                dto.setType(parameter.getType());
                dto.setName(parameter.getName());
                dto.setGenericReturnType(parameter.getParameterizedType());
                list.add(dto);
            }

            contentBuilder.append(writeParameterType(list, contentBuilder));
        });


        contentBuilder.append("service ").append(className).append("GrpcService {\n");

        protoService.getRequestMessages().forEach(protoMessage -> {
            contentBuilder
                    .append("  rpc ")
                    .append(protoMessage.getMessageName().substring(0, 1).toUpperCase()
                            + protoMessage.getMessageName().substring(1))
                    .append(" (")
                    .append(protoMessage.getMessageName().substring(0, 1).toUpperCase()
                            + protoMessage.getMessageName().substring(1)).append("Request) returns (")
                    .append(protoMessage.getMessageName().substring(0, 1).toUpperCase()
                            + protoMessage.getMessageName().substring(1)).append("Response) {}\n");
        });

        contentBuilder.append("}\n");

        return contentBuilder.toString();
    }

    private static TypeDto typeMapper(ParameterDto parameter) {
        TypeDto dto = new TypeDto();
        dto.setCustomFlag(false);
        dto.setDecimalFlag(false);

        Class<?> type = parameter.getType();

        if (type == String.class) {
            dto.setName("string");
        } else if (type == Integer.class) {
            dto.setName("int32");
        } else if (type == Long.class) {
            dto.setName("int64");
        } else if (type == Boolean.class) {
            dto.setName("bool");
        } else if (type == Double.class ||
                type == Float.class ||
                type == BigDecimal.class) {
            dto.setName("DecimalValue");
            dto.setDecimalFlag(true);
            dto.setCustomFlag(true);
        } else if (type == Byte.class) {
            dto.setName("byte");
        } else if (type == Short.class) {
            dto.setName("int32");
        } else if (type == BigInteger.class) {
            dto.setName("int64");
        } else if (type == LocalDateTime.class) {
            dto.setName("google.protobuf.Timestamp");
        } else if (type == List.class) {
            Type genericReturnType = parameter.getGenericReturnType();
            ParameterizedType pType = (ParameterizedType) genericReturnType;
            Class<?> clazz = (Class<?>) pType.getActualTypeArguments()[0];

            ParameterDto listDto = new ParameterDto();
            listDto.setType(clazz);
            listDto.setName(clazz.getName());
            listDto.setAnnotatedType(clazz.getAnnotatedSuperclass());

            TypeDto dto2 = typeMapper(listDto);

            dto.setName("repeated " + dto2.getName());

            if (dto2.getCustomFlag()) {
                dto.setCustomFlag(true);
            }
        } else {
            dto.setName(type.getSimpleName());
            dto.setCustomFlag(true);
        }

        return dto;
    }

    private static StringBuilder writeParameterType(List<ParameterDto> list, StringBuilder currentContentBuilder) {

        StringBuilder contentBuilder = new StringBuilder();
        StringBuilder contentBuilder2 = new StringBuilder();

        Integer index = 1;

        for (ParameterDto parameter : list) {
            TypeDto type = typeMapper(parameter);

            contentBuilder.append("  ").append(type.getName()).append(" ").append(parameter.getName()).append(" = ").append(index + ";\n");

            if (type.getCustomFlag()) {
                String parmName = parameter.getAnnotatedType().toString();

                if (type.getDecimalFlag()) {
                    if (currentContentBuilder.indexOf("message DecimalValue {") == -1) {
                        contentBuilder2.append("message DecimalValue {\n");
                        contentBuilder2.append("  uint32 scale = 1;\n");
                        contentBuilder2.append("  uint32 precision = 2;\n");
                        contentBuilder2.append("  bytes value = 3;\n");
                        contentBuilder2.append("}\n");
                        contentBuilder2.append("\n");
                    }

                    continue;
                }

                try {
                    java.io.File f = new java.io.File(appDir);
                    URL[] cp = {f.toURI().toURL()};
                    URLClassLoader urlcl = new URLClassLoader(cp);
                    Class clazz = urlcl.loadClass(parmName);

                    String header = "message " + parameter.getType().getSimpleName() + " {\n";

                    if (currentContentBuilder.indexOf(header) == -1) {
                        contentBuilder2.append(header);

                        List<ParameterDto> list2 = new ArrayList<>();
                        for (Field field : clazz.getDeclaredFields()) {
                            ParameterDto dto = new ParameterDto();
                            dto.setType(field.getType());
                            dto.setName(field.getName());
                            dto.setAnnotatedType(field.getAnnotatedType());
                            list2.add(dto);
                        }

                        contentBuilder2.append(writeParameterType(list2, currentContentBuilder));
                    }
                } catch (Exception e) {
                    contentBuilder.append("  ").append("???").append(" ")
                            .append(parameter.getName()).append(" = ").append(index + ";\n");
                }
            }
            index++;
        }

        contentBuilder.append("}\n");
        contentBuilder.append("\n");
        contentBuilder.append(contentBuilder2);

        return contentBuilder;
    }

    private static void saveProtobufFiles(CodeGeneratorResponse response, String outputDir) {
        for (File file : response.getFileList()) {
            String filePathString = outputDir + FileSystems.getDefault().getSeparator() + file.getName();
            Path filePath = Paths.get(filePathString);
            try {
                Files.write(filePath, file.getContent().getBytes());
                System.out.println("Protobuf dosyası kaydedildi: " + filePath);
            } catch (IOException e) {
                System.err.println("Protobuf dosyası kaydedilirken bir hata oluştu: " + e.getMessage());
            }
        }
    }
}
