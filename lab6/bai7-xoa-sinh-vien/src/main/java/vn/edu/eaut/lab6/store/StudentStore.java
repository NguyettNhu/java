package vn.edu.eaut.lab6.store;

import vn.edu.eaut.lab6.model.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class StudentStore {
    private static final List<Student> STUDENTS = Collections.synchronizedList(new ArrayList<>());

    private StudentStore() {
    }

    public static List<Student> findAll() {
        synchronized (STUDENTS) {
            return new ArrayList<>(STUDENTS);
        }
    }

    public static List<Student> searchByName(String keyword) {
        String normalized = normalize(keyword);
        if (normalized.isEmpty()) {
            return findAll();
        }

        List<Student> result = new ArrayList<>();
        synchronized (STUDENTS) {
            for (Student student : STUDENTS) {
                if (normalize(student.getName()).contains(normalized)) {
                    result.add(student);
                }
            }
        }
        return result;
    }

    public static Student findById(String id) {
        String normalizedId = normalize(id);
        synchronized (STUDENTS) {
            for (Student student : STUDENTS) {
                if (normalize(student.getId()).equals(normalizedId)) {
                    return copy(student);
                }
            }
        }
        return null;
    }

    public static boolean add(Student student) {
        synchronized (STUDENTS) {
            if (exists(student.getId())) {
                return false;
            }
            STUDENTS.add(copy(student));
            return true;
        }
    }

    public static boolean update(Student updatedStudent) {
        String normalizedId = normalize(updatedStudent.getId());
        synchronized (STUDENTS) {
            for (Student student : STUDENTS) {
                if (normalize(student.getId()).equals(normalizedId)) {
                    student.setName(updatedStudent.getName());
                    student.setClassName(updatedStudent.getClassName());
                    student.setEmail(updatedStudent.getEmail());
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean deleteById(String id) {
        String normalizedId = normalize(id);
        synchronized (STUDENTS) {
            return STUDENTS.removeIf(student -> normalize(student.getId()).equals(normalizedId));
        }
    }

    public static Map<String, Integer> countByClass() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        synchronized (STUDENTS) {
            for (Student student : STUDENTS) {
                stats.merge(student.getClassName(), 1, Integer::sum);
            }
        }
        return stats;
    }

    public static int countAll() {
        synchronized (STUDENTS) {
            return STUDENTS.size();
        }
    }

    public static void replaceAll(List<Student> samples) {
        synchronized (STUDENTS) {
            STUDENTS.clear();
            for (Student student : samples) {
                STUDENTS.add(copy(student));
            }
        }
    }

    private static boolean exists(String id) {
        String normalizedId = normalize(id);
        for (Student student : STUDENTS) {
            if (normalize(student.getId()).equals(normalizedId)) {
                return true;
            }
        }
        return false;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static Student copy(Student student) {
        return new Student(student.getId(), student.getName(), student.getClassName(), student.getEmail());
    }
}
